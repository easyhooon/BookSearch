package com.easyhooon.booksearch.feature.favorites.viewmodel

import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface FavoritesUiAction {
    data class OnBookClick(val book: BookUiModel) : FavoritesUiAction
    data object OnSearchClick : FavoritesUiAction
    data object OnClearClick : FavoritesUiAction
    data object OnSortClick : FavoritesUiAction
    data object OnFilterClick : FavoritesUiAction
}
