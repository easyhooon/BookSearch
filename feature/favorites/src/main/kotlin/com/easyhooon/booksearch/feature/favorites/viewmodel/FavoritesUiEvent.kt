package com.easyhooon.booksearch.feature.favorites.viewmodel

import com.easyhooon.booksearch.core.domain.model.Book

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val book: Book) : FavoritesUiEvent
}
