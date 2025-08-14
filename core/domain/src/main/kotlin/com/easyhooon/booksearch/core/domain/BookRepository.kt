package com.easyhooon.booksearch.core.domain

import java.awt.print.Book

interface BookRepository {
    suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): Result<List<Book>>
}