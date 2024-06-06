package com.mikhailkrivosheev.catalogbrowser.data

import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemDao
import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemEntity
import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemEntity.Companion.toCatalogItem
import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse
import com.mikhailkrivosheev.catalogbrowser.data.models.CatalogItemResponse.Companion.toCatalogItem
import com.mikhailkrivosheev.catalogbrowser.domain.CatalogItem
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryImplTest {

    private val mockCatalogApi: CatalogApi = mockk()
    private val mockCatalogItemDao: CatalogItemDao = mockk()
    private val dispatcher = StandardTestDispatcher()
    private val dispatcherProvider: DispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher
            get() = dispatcher
        override val default: CoroutineDispatcher
            get() = dispatcher
        override val main: CoroutineDispatcher
            get() = dispatcher
    }
    private val repository = RepositoryImpl(mockCatalogApi, dispatcherProvider, mockCatalogItemDao)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test getItems details when loadItems is Success`() = runTest {
        val maxId = "999"
        val catalogItemsList = listOf(
            CatalogItemResponse("text1", 1.2f, "imageUrl1", "Unique123"),
            CatalogItemResponse("text2", 3.14f, "imageUrl2", "Unique321")
        )
        coEvery { mockCatalogApi.getItems(maxId) }.returns(catalogItemsList)
        coEvery { mockCatalogItemDao.getItemByNetworkId(any()) }.returns(null)
        coEvery { mockCatalogItemDao.insertItem(any()) }.just(runs = Runs)
        val expectedResult = ResultData.Success(catalogItemsList.map { it.toCatalogItem() })

        val actualResult = repository.getItems(maxId)

        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `test getItems details when loadItems is not successful`() = runTest {
        val maxId = "999"
        val catalogItemsList = listOf(
            CatalogItemResponse("text1", 1.2f, "imageUrl1", "Unique123"),
            CatalogItemResponse("text2", 3.14f, "imageUrl2", "Unique321")
        )
        coEvery { mockCatalogApi.getItems(maxId) }.returns(catalogItemsList)
        coEvery { mockCatalogItemDao.getItemByNetworkId(any()) }.returns(null)
        coEvery { mockCatalogItemDao.insertItem(any()) }.throws(Throwable())

        val actualResult = repository.getItems(maxId)

        Assert.assertTrue(actualResult is ResultData.Exception<List<CatalogItem>>)
    }



    @Test
    fun `test getItemById when there is the item in database and ResultData is Success`() = runTest {
        val id = "001"
        val expectedCatalogItemEntity = CatalogItemEntity(0, "Text", 1.2f, "imageUrl", "Unique123")
        coEvery { mockCatalogItemDao.getItemByNetworkId(id) }.returns(expectedCatalogItemEntity)
        val expectedResult = ResultData.Success(expectedCatalogItemEntity.toCatalogItem())

        val actualResult = repository.getItemById(id)

        coVerify { mockCatalogItemDao.getItemByNetworkId(id) }
        Assert.assertEquals(expectedResult, actualResult)
    }


    @Test
    fun `test getItemById details when there is no item in database and Exception is Thrown`() = runTest {
        val id = "001"
        val expectedCatalogItemEntity = null
        coEvery { mockCatalogItemDao.getItemByNetworkId(id) }.returns(expectedCatalogItemEntity)

        val actualResult = repository.getItemById(id)

        coVerify { mockCatalogItemDao.getItemByNetworkId(id) }
        Assert.assertTrue(actualResult is ResultData.Exception<CatalogItem>)
    }

}