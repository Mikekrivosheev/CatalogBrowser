package com.mikhailkrivosheev.catalogbrowser.data

import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemDao
import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemEntity
import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemEntity.Companion.toCatalogItem
import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse
import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse.Companion.toCatalogItem
import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse.Companion.toCatalogItemEntity
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem
import com.mikhailkrivosheev.catalogbrowser.domain.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val catalogApi: CatalogApi,
    private val dispatchersProvider: DispatchersProvider,
    private val catalogItemDao: CatalogItemDao
) : Repository {

    override suspend fun getItems(maxId: String?): ResultData<List<CatalogItem>> {
        return withContext(dispatchersProvider.io) {
            try {
                loadItems(maxId)
            } catch (e: Throwable) {
                ResultData.Exception(e)
            }
        }
    }

    override suspend fun getItemById(id: String): ResultData<CatalogItem> {
        return withContext(dispatchersProvider.io) {
            try {
                val item = catalogItemDao.getItemByNetworkId(id)?.toCatalogItem()
                    ?: throwIfNoItemInDatabase(id)
                ResultData.Success(item)
            } catch (e: Throwable) {
                ResultData.Exception(e)
            }
        }
    }

    private suspend fun loadItems(maxId: String?): ResultData.Success<List<CatalogItem>> {
        return try {
            delay(2000)
            val result = catalogApi.getItems(maxId)
            insertNewItems(result)
            ResultData.Success(result.map { it.toCatalogItem() })
        } catch (e: Throwable) {
            val resultDataFromDb = getCachedItems(maxId)
            ResultData.Success(resultDataFromDb.map { it.toCatalogItem() })
        }
    }

    private suspend fun getCachedItems(maxId: String?): List<CatalogItemEntity> {
        return if (maxId == null) {
            catalogItemDao.getNextItems()
        } else {
            val id = catalogItemDao.getItemByNetworkId(maxId)?.id
                ?: throwIfNoItemInDatabase(maxId)
            catalogItemDao.getNextItems(id)
        }
    }

    private suspend fun insertNewItems(networkItems: List<CatalogItemResponse>) {
        val entities = networkItems.map { it.toCatalogItemEntity() }
        entities.forEach { entity ->
            // we check if we already have an item with the same server Id
            val isNewItem = catalogItemDao.getItemByNetworkId(entity._id) == null
            if (isNewItem) {
                catalogItemDao.insertItem(entity)
            }
        }
    }

    private fun throwIfNoItemInDatabase(id: String?): Nothing {
        error("Element with specified id $id does not exist")
    }
}