package com.easyhooon.booksearch.core.domain.model

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
    val status: String
)
