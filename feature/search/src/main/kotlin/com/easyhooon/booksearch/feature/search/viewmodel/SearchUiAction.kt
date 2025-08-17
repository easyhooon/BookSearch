package com.easyhooon.booksearch.feature.search.viewmodel

import com.easyhooon.booksearch.core.domain.model.Book

sealed interface SearchUiAction {
    data class OnBookClick(val book: Book) : SearchUiAction
    data class OnSearchClick(val query: String) : SearchUiAction
    data object OnClearClick : SearchUiAction
    data object OnLoadMore : SearchUiAction
    data object OnRetryClick : SearchUiAction
}
