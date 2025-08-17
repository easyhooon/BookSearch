package com.easyhooon.booksearch.core.common.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class BookUiModel(
    val title: String = "",
    val contents: String = "",
    val url: String = "",
    val isbn: String = "",
    val datetime: String = "",
    val authors: List<String> = listOf(),
    val publisher: String = "",
    val translators: List<String> = listOf(),
    val price: String = "",
    val salePrice: String = "",
    val thumbnail: String = "",
    val status: String = "",
    val isFavorites: Boolean = false,
)
