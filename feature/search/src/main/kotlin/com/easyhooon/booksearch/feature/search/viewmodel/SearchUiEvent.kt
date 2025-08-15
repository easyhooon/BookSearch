package com.easyhooon.booksearch.feature.search.viewmodel

sealed interface SearchUiEvent {
    data class NavigateToDetail(val isbn: String) : SearchUiEvent
}
