package com.easyhooon.booksearch.feature.detail.viewmodel

sealed interface DetailUiAction {
    data object OnBackClick : DetailUiAction
    data object OnFavoritesClick : DetailUiAction
}
