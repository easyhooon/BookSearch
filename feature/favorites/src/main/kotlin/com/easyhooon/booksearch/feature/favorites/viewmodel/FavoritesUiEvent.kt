package com.easyhooon.booksearch.feature.favorites.viewmodel

import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : FavoritesUiEvent
}
