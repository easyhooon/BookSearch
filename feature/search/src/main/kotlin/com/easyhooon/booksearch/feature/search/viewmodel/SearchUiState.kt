package com.easyhooon.booksearch.feature.search.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.easyhooon.booksearch.feature.search.component.FooterState

@Immutable
sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data object Success : SearchState
    data class Error(val message: String) : SearchState
}

data class SearchUiState(
    val searchState: SearchState = SearchState.Idle,
    val footerState: FooterState = FooterState.Idle,
    val queryState: TextFieldState = TextFieldState(),
    val sortType: SortType = SortType.ACCURACY,
    val currentPage: Int = 1,
    val currentQuery: String = "",
    val isLastPage: Boolean = false,
    val totalCount: Int = 0,
) {
    val isEmptySearchResult: Boolean
        get() = searchState is SearchState.Success && totalCount == 0
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
