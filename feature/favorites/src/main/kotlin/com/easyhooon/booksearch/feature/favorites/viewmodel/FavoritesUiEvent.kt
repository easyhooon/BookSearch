package com.easyhooon.booksearch.feature.favorites.viewmodel

import com.easyhooon.booksearch.core.common.UiText
import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : FavoritesUiEvent
    data class ShowToast(val message: UiText) : FavoritesUiEvent
}
