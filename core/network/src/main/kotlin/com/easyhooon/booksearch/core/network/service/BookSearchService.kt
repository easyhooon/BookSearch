package com.easyhooon.booksearch.core.network.service

import com.easyhooon.booksearch.core.network.response.SearchBookResponse
import retrofit2.http.GET

interface BookSearchService {
    @GET("search/book")
    suspend fun searchBooks(
        query: String,
        sort: String = "accuracy",
        page: Int = 1,
        size: Int = 20,
    ): SearchBookResponse
}