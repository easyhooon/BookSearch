package com.easyhooon.booksearch.feature.search.viewmodel

import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface SearchUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : SearchUiEvent
}
