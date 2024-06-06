package com.mikhailkrivosheev.catalogbrowser.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikhailkrivosheev.catalogbrowser.R
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem
import com.mikhailkrivosheev.catalogbrowser.ui.theme.CatalogBrowserTheme

@Composable
fun ListRoute(
    viewModel: CatalogViewModel = hiltViewModel(),
    onItemClick: (CatalogItem) -> Unit
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    ListScreen(
        state = state,
        onLoadMore = {
            viewModel.loadNextItems()
        },
        onItemClick = {
            onItemClick(it)
        },
        onErrorMessageShown = {
            viewModel.onErrorMessageShown()
        },
        onPullToRefresh = {
            viewModel.onPullToRefresh()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreen(
    state: CatalogState,
    onLoadMore: () -> Unit,
    onItemClick: (CatalogItem) -> Unit,
    onErrorMessageShown: () -> Unit,
    onPullToRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onPullToRefresh()
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }


    val snackbarHostState = remember { SnackbarHostState() }
    if (state.errorMessage != null) {
        LaunchedEffect(state.errorMessage) {
            snackbarHostState.showSnackbar(message = state.errorMessage)
            onErrorMessageShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            ItemsListView(
                state = state,
                onLoadMore = onLoadMore,
                onItemClick = onItemClick
            )

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState
            )
        }
    }
}

@Composable
private fun ItemsListView(
    state: CatalogState,
    onLoadMore: () -> Unit,
    onItemClick: (CatalogItem) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(all = 8.dp)
    ) {
        items(state.items, key = { item: CatalogItem -> item.id }) { item ->
            val isLastItem = state.lastItemId == item.id
            val notFinish = !state.endReached
            if (notFinish && isLastItem && state.notLoading) {
                onLoadMore()
            }

            ItemView(catalogItem = item, onItemClick = onItemClick)
        }
        if (state.isLoadingMore) {
            item { CircularProgressIndicator(modifier = Modifier.size(100.dp)) }
        }
    }
}

@Composable
fun ItemView(
    catalogItem: CatalogItem,
    onItemClick: ((CatalogItem) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (onItemClick != null) {
                    Modifier
                        .clip(CardDefaults.shape)
                        .clickable { onItemClick(catalogItem) }
                } else {
                    Modifier
                }
            ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            SpacerHeight(8.dp)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(catalogItem.image)
                    .crossfade(true)
                    .allowHardware(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            SpacerHeight(8.dp)
            Text(catalogItem.text)
            SpacerHeight(8.dp)
            Text(catalogItem.id)
            SpacerHeight(8.dp)
            Text(catalogItem.confidence.toString())
            SpacerHeight(8.dp)
        }
    }
}

@Composable
private fun SpacerHeight(
    height: Dp = 0.dp,
) {
    Spacer(modifier = Modifier.padding(top = height))
}


@Preview
@Composable
private fun ItemListPreview() {
    CatalogBrowserTheme {
        ItemsListView(
            CatalogState(
                items = List(2) {
                    CatalogItem("Text = $it", it.toFloat(), "", it.toString())
                },
                isLoadingMore = true
            ),
            {},
            {}
        )
    }
}