package com.easyhooon.booksearch.core.network.client

import com.easyhooon.booksearch.core.network.response.SearchBookResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class BookSearchKtorClient @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun searchBook(
        query: String,
        sort: String = "accuracy",
        page: Int = 1,
        size: Int = 20,
    ): SearchBookResponse {
        return httpClient.get("search/book") {
            parameter("query", query)
            parameter("sort", sort)
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
}
