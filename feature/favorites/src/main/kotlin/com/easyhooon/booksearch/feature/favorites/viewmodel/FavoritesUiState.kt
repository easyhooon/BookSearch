package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import com.easyhooon.booksearch.core.domain.usecase.FavoritesSortType

data class FavoritesUiState(
    val queryState: TextFieldState = TextFieldState(),
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.TITLE_ASC,
    val isPriceFilterEnabled: Boolean = false,
)

