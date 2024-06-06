package com.mikhailkrivosheev.catalogbrowser.ui.details

import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem

data class DetailsState(
    val catalogItem: CatalogItem? = null,
    val errorMessage: String? = null
)