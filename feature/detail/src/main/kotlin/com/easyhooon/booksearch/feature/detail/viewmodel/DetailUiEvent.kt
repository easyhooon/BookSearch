package com.easyhooon.booksearch.feature.detail.viewmodel

import com.easyhooon.booksearch.core.common.UiText

sealed interface DetailUiEvent {
    data object NavigateBack : DetailUiEvent
    data class ShowToast(val message: UiText) : DetailUiEvent
}
