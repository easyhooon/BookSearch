package com.easyhooon.booksearch.feature.favorites.viewmodel

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val isbn: String) : FavoritesUiEvent
}
