package com.easyhooon.booksearch.feature.favorites.viewmodel

import com.easyhooon.booksearch.core.domain.model.Book

sealed interface FavoritesUiAction {
    data class OnBookClick(val book: Book) : FavoritesUiAction
    data object OnSearchClick : FavoritesUiAction
    data object OnClearClick : FavoritesUiAction
}
