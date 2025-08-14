package com.easyhooon.booksearch.core.database

import androidx.room.Database
import com.easyhooon.booksearch.core.database.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class FavoritesDatabase : androidx.room.RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        const val DATABASE_NAME = "favorites_database"
    }
}