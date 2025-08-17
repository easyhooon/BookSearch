package com.easyhooon.booksearch.feature.search.viewmodel

import com.easyhooon.booksearch.core.domain.model.Book

sealed interface SearchUiEvent {
    data class NavigateToDetail(val book: Book) : SearchUiEvent
}
