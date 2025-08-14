package com.easyhooon.booksearch.core.database

import androidx.room.Database
import androidx.room.TypeConverters
import com.easyhooon.booksearch.core.database.converter.StringListConverter
import com.easyhooon.booksearch.core.database.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class FavoritesDatabase : androidx.room.RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        const val DATABASE_NAME = "favorites_database"
    }
}
