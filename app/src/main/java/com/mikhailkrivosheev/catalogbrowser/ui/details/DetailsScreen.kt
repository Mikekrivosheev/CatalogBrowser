package com.mikhailkrivosheev.catalogbrowser.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikhailkrivosheev.catalogbrowser.ui.catalog.ItemView

@Composable
fun DetailsRoute(
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    DetailsScreen(
        state = state,
        onErrorMessageShown = {
            viewModel.onErrorMessageShown()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onErrorMessageShown: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    if (state.errorMessage != null) {
        LaunchedEffect(state.errorMessage) {
            snackbarHostState.showSnackbar(message = state.errorMessage)
            onErrorMessageShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Details")
                }
            )
        }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding)
        ) {
            state.catalogItem?.let { ItemView(it) }
        }
    }
}