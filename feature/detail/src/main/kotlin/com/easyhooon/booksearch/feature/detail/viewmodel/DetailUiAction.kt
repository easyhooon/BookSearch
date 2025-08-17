package com.easyhooon.booksearch.feature.detail.viewmodel

import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface DetailUiAction {
    data object OnBackClick : DetailUiAction
    data class OnFavoritesClick(val book: BookUiModel) : DetailUiAction
}
