package com.easyhooon.booksearch.feature.search.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.ui.component.FooterState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data object Success : SearchState
    data class Error(val exception: Throwable) : SearchState
}

data class SearchUiState(
    val searchState: SearchState = SearchState.Idle,
    val footerState: FooterState = FooterState.Idle,
    val queryState: TextFieldState = TextFieldState(),
    val books: ImmutableList<BookUiModel> = persistentListOf(),
    val sortType: SortType = SortType.ACCURACY,
    val currentPage: Int = 1,
    val currentQuery: String = "",
    val isLastPage: Boolean = false,
) {
    val isEmptySearchResult: Boolean
        get() = searchState is SearchState.Success && books.isEmpty()
}

enum class SortType(val value: String, val label: String) {
    ACCURACY("accuracy", "정확도순"),
    LATEST("latest", "발간일순"),
    ;

    fun toggle(): SortType {
        return when (this) {
            ACCURACY -> LATEST
            LATEST -> ACCURACY
        }
    }
}
