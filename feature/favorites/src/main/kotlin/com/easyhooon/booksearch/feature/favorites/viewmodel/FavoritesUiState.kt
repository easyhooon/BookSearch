package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.TextFieldState

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val queryState: TextFieldState = TextFieldState(),
    val searchQuery: String = "",
)
