package com.easyhooon.booksearch.core.domain.repository

import com.easyhooon.booksearch.core.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun searchFavoritesByTitle(query: String): Flow<List<Book>>

    suspend fun insertBook(book: Book)
    suspend fun deleteBook(isbn: String)
    val favoriteBooks: Flow<List<Book>>
}
