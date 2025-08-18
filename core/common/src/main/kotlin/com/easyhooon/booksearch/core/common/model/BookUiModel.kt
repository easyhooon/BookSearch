package com.easyhooon.booksearch.core.common.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class BookUiModel(
    val title: String = "",
    val contents: String = "",
    val url: String = "",
    val isbn: String = "",
    val datetime: String = "",
    val authors: ImmutableList<String> = persistentListOf(),
    val publisher: String = "",
    val translators: ImmutableList<String> = persistentListOf(),
    val price: String = "",
    val salePrice: String = "",
    val thumbnail: String = "",
    val status: String = "",
    val isFavorites: Boolean = false,
)
