package com.easyhooon.booksearch.feature.detail.viewmodel

sealed interface DetailUiAction {
    data object OnNackClick: DetailUiAction
}
