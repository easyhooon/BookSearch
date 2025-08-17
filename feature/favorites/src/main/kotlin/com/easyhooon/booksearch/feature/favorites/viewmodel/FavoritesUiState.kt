package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.TextFieldState

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val queryState: TextFieldState = TextFieldState(),
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.TITLE_ASC,
)

enum class FavoritesSortType(val label: String) {
    TITLE_ASC("제목 오름차순"),
    TITLE_DESC("제목 내림차순"),
    ;

    fun toggle(): FavoritesSortType {
        return when (this) {
            TITLE_ASC -> TITLE_DESC
            TITLE_DESC -> TITLE_ASC
        }
    }
}
