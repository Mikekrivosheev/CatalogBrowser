package com.mikhailkrivosheev.catalogbrowser.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem
import com.mikhailkrivosheev.catalogbrowser.domain.Paginator
import com.mikhailkrivosheev.catalogbrowser.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(CatalogState())
    val stateFlow = mutableStateFlow.asStateFlow()

    private val paginator = Paginator<String, CatalogItem>(
        initialKey = null, // offset
        onLoadUpdated = { isLoading ->
            if (mutableStateFlow.value.items.isNotEmpty()) {
                mutableStateFlow.update { it.copy(isLoading = false, isLoadingMore = isLoading) }
            } else {
                mutableStateFlow.update { it.copy(isLoading = isLoading) }
            }
        },
        onRequest = { offset -> repository.getItems(offset) },
        getNextKey = { _, items -> items.lastOrNull()?.id },
        onError = { errorMessage -> mutableStateFlow.update { it.copy(errorMessage = errorMessage) } },
        onSuccess = { items, _ -> addNewItems(items) },
        onReset = { mutableStateFlow.update { it.copy(items = emptyList()) } }
    )

    init {
        loadNextItems()
    }

    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun onErrorMessageShown() {
        mutableStateFlow.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            paginator.reset()
            paginator.loadNextItems()
        }
    }

    private fun addNewItems(items: List<CatalogItem>) {
        mutableStateFlow.update { it.copy(items = it.items + items, endReached = items.isEmpty()) }
    }

    override fun onCleared() {
        super.onCleared()
        paginator.reset()
    }
}