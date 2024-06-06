package com.mikhailkrivosheev.catalogbrowser.domain

data class CatalogItem(
    val text: String,
    val confidence: Float,
    val image: String,
    val id: String
)
