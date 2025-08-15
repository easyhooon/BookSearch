package com.easyhooon.booksearch.feature.favorites.viewmodel

sealed interface FavoritesUiAction {
    data class OnBookCardClick(val isbn: String): FavoritesUiAction
}
