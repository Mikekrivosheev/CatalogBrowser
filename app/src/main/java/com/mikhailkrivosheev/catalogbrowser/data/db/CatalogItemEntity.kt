package com.mikhailkrivosheev.catalogbrowser.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem

@Entity(tableName = "CatalogBrowser")
data class CatalogItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("_text")
    val text: String,
    @ColumnInfo("_confidence")
    val confidence: Float,
    @ColumnInfo("_url")
    val image: String,
    @ColumnInfo("_id")
    val _id: String
) {
    companion object {
        fun CatalogItemEntity.toCatalogItem() =
            CatalogItem(
                text = text,
                confidence = confidence,
                image = image,
                id = _id
            )
    }
}

