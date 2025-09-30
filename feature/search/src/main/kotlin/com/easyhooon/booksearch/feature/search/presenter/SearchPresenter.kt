package com.easyhooon.booksearch.feature.search.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.EventFlow
import com.easyhooon.booksearch.core.common.compose.providePresenterDefaults
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.SearchScreenContext
import com.easyhooon.booksearch.feature.search.SortType
import com.orhanobut.logger.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchUiState(
    val currentQuery: String = "",
    val sortType: SortType = SortType.ACCURACY,
    val searchResults: ImmutableList<BookUiModel> = persistentListOf(),
    val hasNextPage: Boolean = false,
)

sealed interface SearchScreenEvent {
    data class Search(val query: String) : SearchScreenEvent
    data object ClearSearch : SearchScreenEvent
    data object ToggleSort : SearchScreenEvent
}

context(context: SearchScreenContext)
@Composable
fun SearchPresenter(
    eventFlow: EventFlow<SearchScreenEvent>,
    queryState: TextFieldState,
    currentQuery: String,
    sortType: SortType,
    searchResults: ImmutableList<BookUiModel>,
    hasNextPage: Boolean,
    onQueryChange: (String) -> Unit,
    onSortChange: (SortType) -> Unit,
): SearchUiState = providePresenterDefaults {
    EventEffect(eventFlow) { event ->
        Logger.d("SearchPresenter: Received event: $event")
        when (event) {
            is SearchScreenEvent.Search -> {
                Logger.d("SearchPresenter: Search event called with query: '${event.query}'")
                if (event.query.isNotBlank()) {
                    Logger.d("SearchPresenter: Query is not blank, updating currentQuery to '${event.query}'")
                    onQueryChange(event.query)
                } else {
                    Logger.d("SearchPresenter: Query is blank, ignoring")
                }
            }
            is SearchScreenEvent.ClearSearch -> {
                queryState.clearText()
                onQueryChange("")
            }
            is SearchScreenEvent.ToggleSort -> {
                Logger.d("SearchPresenter: ToggleSort event - current: ${sortType}, toggling to: ${sortType.toggle()}")
                onSortChange(sortType.toggle())
            }
        }
    }

    SearchUiState(
        currentQuery = currentQuery,
        sortType = sortType,
        searchResults = searchResults,
        hasNextPage = hasNextPage,
    )
}
