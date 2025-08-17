package com.easyhooon.booksearch.core.network.service

import com.easyhooon.booksearch.core.network.response.SearchBookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BookSearchService {
    @GET("search/book")
    suspend fun searchBook(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): SearchBookResponse
}
