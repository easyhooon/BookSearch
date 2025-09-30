package com.easyhooon.booksearch.feature.search.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.EventFlow
import com.easyhooon.booksearch.core.common.compose.providePresenterDefaults
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.orhanobut.logger.Logger
import com.easyhooon.booksearch.feature.search.SearchScreenContext
import com.easyhooon.booksearch.feature.search.SortType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import soil.query.compose.rememberMutation

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
    data class ToggleFavorite(val book: BookUiModel) : SearchScreenEvent
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
    // 즐겨찾기 토글 mutation
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }

    EventEffect(eventFlow) { event ->
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
                onSortChange(sortType.toggle())
            }
            is SearchScreenEvent.ToggleFavorite -> {
                toggleMutationKey = context.createToggleFavoriteQueryKey(event.book)
                toggleFavoriteMutation?.mutate(Unit)
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
