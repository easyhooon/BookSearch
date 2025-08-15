package com.easyhooon.booksearch.feature.search.viewmodel

sealed interface SearchUiAction {
    data class OnBookCardClick(val isbn: String): SearchUiAction
}
