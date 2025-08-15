package com.easyhooon.booksearch.feature.detail.viewmodel

sealed interface DetailUiEvent {
    data object NavigateBack: DetailUiEvent
}
