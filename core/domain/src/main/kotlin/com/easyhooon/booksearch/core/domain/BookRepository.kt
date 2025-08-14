package com.easyhooon.booksearch.core.domain

import com.easyhooon.booksearch.core.domain.model.Book

interface BookRepository {
    suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): Result<List<Book>>
}