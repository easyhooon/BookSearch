package com.easyhooon.booksearch.core.domain.model

data class SearchBook(
    val meta: Meta,
    val documents: List<Book>,
)

data class Meta(
    val totalCount: Int,
    val pageableCount: Int,
    val isEnd: Boolean,
)

data class Book(
    val title: String,
    val contents: String,
    val url: String,
    val isbn: String,
    val datetime: String,
    val authors: List<String>,
    val publisher: String,
    val translators: List<String>,
    val price: String,
    val salePrice: String,
    val thumbnail: String,
    val status: String,
    val isFavorite: Boolean = false,
)
