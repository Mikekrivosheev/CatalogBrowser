package com.mikhailkrivosheev.catalogbrowser.ui.catalog

import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem

data class CatalogState(
    val items: List<CatalogItem> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val clickedItemMessage: String? = null,
) {
    val notLoading: Boolean
        get() = !isLoading && !isLoadingMore

    val lastItemId: String?
        get() = items.lastOrNull()?.id
}
