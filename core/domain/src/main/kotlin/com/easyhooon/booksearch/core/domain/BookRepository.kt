package com.easyhooon.booksearch.core.domain

import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.model.SearchBook
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): SearchBook

    fun searchFavoritesByTitle(query: String): Flow<List<Book>>

    suspend fun insertBook(book: Book)
    suspend fun deleteBook(isbn: String)
    val favoriteBooks: Flow<List<Book>>
}
