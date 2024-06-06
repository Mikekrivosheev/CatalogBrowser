package com.mikhailkrivosheev.catalogbrowser.domain

import com.mikhailkrivosheev.catalogbrowser.data.ResultData

interface Repository {
    suspend fun getItems(maxId: String?): ResultData<List<CatalogItem>>
    suspend fun getItemById(id: String): ResultData<CatalogItem>
}