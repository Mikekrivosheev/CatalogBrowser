package com.mikhailkrivosheev.catalogbrowser.data.models

import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemEntity
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem

data class CatalogItemResponse(
    val text: String,
    val confidence: Float,
    val image: String,
    val _id: String
) {
    companion object {
        fun CatalogItemResponse.toCatalogItem() =
            CatalogItem(
                text = text,
                confidence = confidence,
                image = image,
                id = _id
            )

        fun CatalogItemResponse.toCatalogItemEntity() =
            CatalogItemEntity(
                text = text,
                confidence = confidence,
                image = image,
                _id = _id
            )
    }
}
