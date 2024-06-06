package com.mikhailkrivosheev.catalogbrowser.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CatalogItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(catalogItemEntitiesList: List<CatalogItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(catalogItem: CatalogItemEntity)

    @Query("SELECT * FROM CatalogBrowser")
    suspend fun getAll(): List<CatalogItemEntity>

    @Query("SELECT * FROM CatalogBrowser WHERE :networkId =_id")
    suspend fun getItemByNetworkId(networkId: String): CatalogItemEntity?

    @Query("SELECT * FROM CatalogBrowser WHERE id > :specifiedId limit 10")
    suspend fun getNextItems(specifiedId: Int): List<CatalogItemEntity>

    @Query("SELECT * FROM CatalogBrowser limit 10")
    suspend fun getNextItems(): List<CatalogItemEntity>
}
