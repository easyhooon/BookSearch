package com.easyhooon.booksearch.feature.detail.viewmodel

import com.easyhooon.booksearch.core.domain.model.Book

sealed interface DetailUiAction {
    data object OnBackClick : DetailUiAction
    data class OnFavoritesClick(val book: Book) : DetailUiAction
}
