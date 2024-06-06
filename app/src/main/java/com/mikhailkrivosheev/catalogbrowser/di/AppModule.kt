package com.mikhailkrivosheev.catalogbrowser.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.mikhailkrivosheev.catalogbrowser.BuildConfig
import com.mikhailkrivosheev.catalogbrowser.data.CatalogApi
import com.mikhailkrivosheev.catalogbrowser.data.CoroutineDispatcherProvider
import com.mikhailkrivosheev.catalogbrowser.data.DispatchersProvider
import com.mikhailkrivosheev.catalogbrowser.data.RepositoryImpl
import com.mikhailkrivosheev.catalogbrowser.data.db.AppDatabase
import com.mikhailkrivosheev.catalogbrowser.data.db.CatalogItemDao
import com.mikhailkrivosheev.catalogbrowser.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson =
        Gson()
            .newBuilder()
            .create()


    @Provides
    @Singleton
    fun provideCatalogApi(gson: Gson): CatalogApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(
                OkHttpClient.Builder()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            addInterceptor(
                                HttpLoggingInterceptor().apply {
                                    level = HttpLoggingInterceptor.Level.HEADERS
                                }
                            )
                        }
                        addInterceptor { chain ->
                            val request = chain.request()
                            val newRequest = request.newBuilder().header(
                                "Authorization",
                                BuildConfig.Authorization
                            ).build()
                            chain.proceed(newRequest)
                        }
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CatalogApi::class.java)
    }


    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
        return builder.build()
    }

    @Singleton
    @Provides
    fun providesCatalogItemDao(db: AppDatabase) = db.catalogItemDao()

    @Provides
    fun provideDispatchers(): DispatchersProvider = CoroutineDispatcherProvider()

    @Provides
    @Singleton
    fun provideRepository(
        api: CatalogApi,
        dispatcherProvider: DispatchersProvider,
        catalogItemDao: CatalogItemDao
    ): Repository = RepositoryImpl(api, dispatcherProvider, catalogItemDao)

}