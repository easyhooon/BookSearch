package com.easyhooon.booksearch.feature.search.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import android.util.Log
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.SearchScreenContext
import com.easyhooon.booksearch.feature.search.SortType
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import soil.query.compose.rememberInfiniteQuery
import soil.query.compose.rememberMutation
import soil.query.compose.rememberSubscription
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import soil.query.annotation.ExperimentalSoilQueryApi

data class SearchUiState(
    val currentQuery: String = "",
    val sortType: SortType = SortType.ACCURACY,
    val searchResults: ImmutableList<BookUiModel> = persistentListOf(),
    val hasNextPage: Boolean = false,
)

sealed interface SearchUiAction {
    data class OnSearchClick(val query: String) : SearchUiAction
    data object OnClearClick : SearchUiAction
    data object OnSortClick : SearchUiAction
    data class OnBookClick(val book: BookUiModel) : SearchUiAction
    data class OnFavoriteToggle(val book: BookUiModel) : SearchUiAction
    data object OnLoadMore : SearchUiAction
}

sealed interface SearchUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : SearchUiEvent
}

data class SearchPresenterState(
    val uiState: SearchUiState,
    val onAction: (SearchUiAction) -> Unit,
)

@OptIn(ExperimentalSoilQueryApi::class)
context(context: SearchScreenContext)
@Composable
fun SearchPresenter(
    queryState: TextFieldState,
    eventFlow: MutableSharedFlow<SearchUiEvent>,
): SearchPresenterState = providePresenterDefaults {
    var currentQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(SortType.ACCURACY) }
    val coroutineScope = rememberCoroutineScope()


    // API 호출 - InfiniteQuery로 검색 결과 가져오기 (쿼리가 있을 때만)
    val infiniteQuery = if (currentQuery.isNotEmpty()) {
        Log.d("SearchPresenter", "Creating InfiniteQuery for query: $currentQuery")
        rememberInfiniteQuery(
            key = context.searchBooksQueryKey.create(
                query = currentQuery,
                sort = sortType.value,
                size = 20,
            )
        )
    } else {
        Log.d("SearchPresenter", "currentQuery is empty, not creating InfiniteQuery")
        null
    }

    // 즐겨찾기 ID 구독
    val favoriteIdsSubscription = rememberSubscription(
        key = context.createFavoriteBookIdsSubscriptionKey()
    )

    val favoriteBookIds = favoriteIdsSubscription.data ?: emptySet()

    // 검색 결과에 즐겨찾기 상태 반영
    val rawSearchResults = infiniteQuery?.data?.flatMap { it.data } ?: emptyList()
    val searchResults = rawSearchResults.map { book ->
        book.copy(isFavorites = favoriteBookIds.contains(book.isbn))
    }.toImmutableList()

    val hasNextPage = infiniteQuery?.loadMoreParam != null

    // 즐겨찾기 토글 mutation
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }

    val onAction: (SearchUiAction) -> Unit = remember(currentQuery, sortType, infiniteQuery) {
        { action ->
            when (action) {
                is SearchUiAction.OnSearchClick -> {
                    Log.d("SearchPresenter", "OnSearchClick called with query: '${action.query}'")
                    if (action.query.isNotBlank()) {
                        Log.d("SearchPresenter", "Query is not blank, updating currentQuery from '$currentQuery' to '${action.query}'")
                        currentQuery = action.query
                    } else {
                        Log.d("SearchPresenter", "Query is blank, ignoring")
                    }
                }
                is SearchUiAction.OnClearClick -> {
                    queryState.clearText()
                    currentQuery = ""
                }
                is SearchUiAction.OnSortClick -> {
                    sortType = sortType.toggle()
                }
                is SearchUiAction.OnBookClick -> {
                    eventFlow.tryEmit(SearchUiEvent.NavigateToDetail(action.book))
                }
                is SearchUiAction.OnFavoriteToggle -> {
                    coroutineScope.launch {
                        toggleMutationKey = context.createToggleFavoriteQueryKey(action.book)
                        toggleFavoriteMutation?.mutateAsync(Unit)
                    }
                }
                is SearchUiAction.OnLoadMore -> {
                    infiniteQuery?.let { query ->
                        coroutineScope.launch {
                            query.loadMore(query.loadMoreParam!!)
                        }
                    }
                }
            }
        }
    }

    val uiState = SearchUiState(
        currentQuery = currentQuery,
        sortType = sortType,
        searchResults = searchResults,
        hasNextPage = hasNextPage,
    )

    return SearchPresenterState(
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
private inline fun providePresenterDefaults(
    content: @Composable () -> SearchPresenterState
): SearchPresenterState = content()
