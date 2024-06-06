package com.mikhailkrivosheev.catalogbrowser.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CatalogItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogItemDao(): CatalogItemDao

    companion object {
        const val DATABASE_NAME = "catablogbrowserdb"
    }
}

