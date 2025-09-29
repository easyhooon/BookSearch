package com.easyhooon.booksearch.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easyhooon.booksearch.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<BookEntity>>

    @Query("SELECT * FROM favorites WHERE title LIKE '%' || :query || '%' COLLATE NOCASE")
    fun searchFavoritesByTitle(query: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(book: BookEntity)

    @Query("DELETE FROM favorites WHERE isbn = :isbn")
    suspend fun deleteFavorite(isbn: String): Int
}
