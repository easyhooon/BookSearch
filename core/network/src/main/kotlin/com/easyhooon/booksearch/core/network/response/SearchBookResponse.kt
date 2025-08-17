package com.easyhooon.booksearch.core.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchBookResponse(
    @SerialName("meta")
    val meta: MetaResponse,
    @SerialName("documents")
    val documents: List<BookResponse>,
)

@Serializable
data class MetaResponse(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("pageable_count")
    val pageableCount: Int,
    @SerialName("is_end")
    val isEnd: Boolean,
)

@Serializable
data class BookResponse(
    @SerialName("title")
    val title: String,
    @SerialName("contents")
    val contents: String,
    @SerialName("url")
    val url: String,
    @SerialName("isbn")
    val isbn: String,
    @SerialName("datetime")
    val datetime: String,
    @SerialName("authors")
    val authors: List<String>,
    @SerialName("publisher")
    val publisher: String,
    @SerialName("translators")
    val translators: List<String>,
    @SerialName("price")
    val price: String,
    @SerialName("sale_price")
    val salePrice: String,
    @SerialName("thumbnail")
    val thumbnail: String,
    @SerialName("status")
    val status: String,
)
