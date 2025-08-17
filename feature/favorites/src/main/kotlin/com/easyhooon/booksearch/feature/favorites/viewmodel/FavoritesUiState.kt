package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import com.easyhooon.booksearch.core.common.model.SortType

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val queryState: TextFieldState = TextFieldState(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.ACCURACY,
)
