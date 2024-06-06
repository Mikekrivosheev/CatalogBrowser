package com.mikhailkrivosheev.catalogbrowser.data

import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApi {

    @GET("/e/mock/v1/items")
    suspend fun getItems(@Query("max_id") sinceId: String? = null): List<CatalogItemResponse>

}