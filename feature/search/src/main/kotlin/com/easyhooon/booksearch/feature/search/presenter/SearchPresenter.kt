package com.easyhooon.booksearch.feature.search.presenter

import android.util.Log
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
import com.easyhooon.booksearch.feature.search.SearchScreenContext
import com.easyhooon.booksearch.feature.search.SortType
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberInfiniteQuery
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
    data object LoadMore : SearchScreenEvent
}

@OptIn(ExperimentalSoilQueryApi::class)
context(context: SearchScreenContext)
@Composable
fun SearchPresenter(
    eventFlow: EventFlow<SearchScreenEvent>,
    queryState: TextFieldState,
    favoriteBookIds: Set<String> = emptySet(),
): SearchUiState = providePresenterDefaults {
    var currentQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(SortType.ACCURACY) }

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

    // 검색 결과에 즐겨찾기 상태 반영 (favoriteBookIds는 파라미터로 받음)
    val rawSearchResults = infiniteQuery?.data?.flatMap { it.data } ?: emptyList()
    val searchResults = remember(rawSearchResults, favoriteBookIds) {
        rawSearchResults.map { book ->
            val isFavorite = favoriteBookIds.contains(book.isbn)
            Log.d("SearchPresenter", "Book: ${book.title}, ISBN: ${book.isbn}, isFavorite: $isFavorite, favoriteIds: $favoriteBookIds")
            book.copy(isFavorites = isFavorite)
        }.toImmutableList()
    }

    val hasNextPage = infiniteQuery?.loadMoreParam != null

    // 즐겨찾기 토글 mutation
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }

    EventEffect(eventFlow) { event ->
        when (event) {
            is SearchScreenEvent.Search -> {
                Log.d("SearchPresenter", "Search event called with query: '${event.query}'")
                if (event.query.isNotBlank()) {
                    Log.d("SearchPresenter", "Query is not blank, updating currentQuery from '$currentQuery' to '${event.query}'")
                    currentQuery = event.query
                } else {
                    Log.d("SearchPresenter", "Query is blank, ignoring")
                }
            }
            is SearchScreenEvent.ClearSearch -> {
                queryState.clearText()
                currentQuery = ""
            }
            is SearchScreenEvent.ToggleSort -> {
                sortType = sortType.toggle()
            }
            is SearchScreenEvent.ToggleFavorite -> {
                toggleMutationKey = context.createToggleFavoriteQueryKey(event.book)
                toggleFavoriteMutation?.mutate(Unit)
            }
            is SearchScreenEvent.LoadMore -> {
                infiniteQuery?.let { query ->
                    query.loadMore(query.loadMoreParam!!)
                }
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
