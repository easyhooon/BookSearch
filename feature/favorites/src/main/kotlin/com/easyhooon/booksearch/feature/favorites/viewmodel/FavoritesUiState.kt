package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.TextFieldState

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val queryState: TextFieldState = TextFieldState(),
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.TITLE_ASC,
    val isPriceFilterEnabled: Boolean = false,
)

enum class FavoritesSortType(val label: String) {
    TITLE_ASC("오름차순(제목)"),
    TITLE_DESC("내림차순(제목)"),
    ;

    fun toggle(): FavoritesSortType {
        return when (this) {
            TITLE_ASC -> TITLE_DESC
            TITLE_DESC -> TITLE_ASC
        }
    }
}
