package com.easyhooon.booksearch.feature.search.viewmodel

import com.easyhooon.booksearch.core.common.model.BookUiModel

sealed interface SearchUiAction {
    data class OnBookClick(val book: BookUiModel) : SearchUiAction
    data class OnSearchClick(val query: String) : SearchUiAction
    data object OnClearClick : SearchUiAction
    data object OnLoadMore : SearchUiAction
    data object OnRetryClick : SearchUiAction
    data object OnSortClick : SearchUiAction
}
