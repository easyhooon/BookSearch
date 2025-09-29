package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.SoilDataBoundary
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import com.easyhooon.booksearch.feature.search.presenter.SearchScreenEvent
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberInfiniteQuery
import soil.query.compose.rememberSubscription

@OptIn(ExperimentalSoilQueryApi::class)
context(context: SearchScreenContext)
@Composable
fun SearchScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    val eventFlow = rememberEventFlow<SearchScreenEvent>()

    // 검색 쿼리와 정렬 상태 관리
    var currentQuery by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(SortType.ACCURACY) }

    // 즐겨찾기 구독
    val favoriteIdsSubscription = rememberSubscription(
        key = context.createFavoriteBookIdsSubscriptionKey()
    )

    // 검색 쿼리가 있을 때만 InfiniteQuery 생성
    val searchInfiniteQuery = if (currentQuery.isNotEmpty()) {
        rememberInfiniteQuery(
            key = context.searchBooksQueryKey.create(
                query = currentQuery,
                sort = sortType.value,
                size = 20,
            )
        )
    } else null

    if (searchInfiniteQuery != null) {
        // 검색 결과가 있는 경우: 두 상태 combine
        SoilDataBoundary(
            state1 = favoriteIdsSubscription,
            state2 = searchInfiniteQuery,
            fallback = com.easyhooon.booksearch.core.common.SoilFallback(
                errorFallback = {
                    val uiState = SearchPresenter(
                        eventFlow = eventFlow,
                        queryState = queryState,
                        currentQuery = currentQuery,
                        sortType = sortType,
                        searchResults = persistentListOf(),
                        hasNextPage = false,
                        onQueryChange = { currentQuery = it },
                        onSortChange = { sortType = it },
                    )

                    SearchScreen(
                        innerPadding = innerPadding,
                        queryState = queryState,
                        uiState = uiState,
                        onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                        onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                        onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                        onLoadMore = { eventFlow.tryEmit(SearchScreenEvent.LoadMore) },
                        onBookClick = onBookClick,
                    )
                },
                suspenseFallback = {
                    val uiState = SearchPresenter(
                        eventFlow = eventFlow,
                        queryState = queryState,
                        currentQuery = currentQuery,
                        sortType = sortType,
                        searchResults = persistentListOf(),
                        hasNextPage = false,
                        onQueryChange = { currentQuery = it },
                        onSortChange = { sortType = it },
                    )

                    SearchScreen(
                        innerPadding = innerPadding,
                        queryState = queryState,
                        uiState = uiState,
                        onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                        onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                        onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                        onLoadMore = { eventFlow.tryEmit(SearchScreenEvent.LoadMore) },
                        onBookClick = onBookClick,
                    )
                }
            )
        ) { favoriteBookIds, searchQuery ->
            // Soil 패턴: chunkedData로 모든 페이지의 데이터를 평탄화
            val allSearchResults = searchQuery.data?.flatMap { it } ?: emptyList()

            // 검색 결과에 즐겨찾기 상태 반영
            val searchResults = allSearchResults.map { book ->
                book.copy(isFavorites = favoriteBookIds.contains(book.isbn))
            }.toImmutableList()

            val uiState = SearchPresenter(
                eventFlow = eventFlow,
                queryState = queryState,
                currentQuery = currentQuery,
                sortType = sortType,
                searchResults = searchResults,
                hasNextPage = searchQuery.loadMoreParam != null,
                onQueryChange = { currentQuery = it },
                onSortChange = { sortType = it },
            )

            SearchScreen(
                innerPadding = innerPadding,
                queryState = queryState,
                uiState = uiState,
                onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                onLoadMore = {
                    // Soil 패턴: loadMoreParam이 있으면 loadMore 실행
                    searchQuery.loadMoreParam?.let { param ->
                        searchQuery.loadMore(param)
                    }
                },
                onBookClick = onBookClick,
            )
        }
    } else {
        // 검색 결과가 없는 경우: 즐겨찾기 상태만
        SoilDataBoundary(
            state = favoriteIdsSubscription,
            fallback = com.easyhooon.booksearch.core.common.SoilFallback(
                errorFallback = {
                    val uiState = SearchPresenter(
                        eventFlow = eventFlow,
                        queryState = queryState,
                        currentQuery = currentQuery,
                        sortType = sortType,
                        searchResults = persistentListOf(),
                        hasNextPage = false,
                        onQueryChange = { currentQuery = it },
                        onSortChange = { sortType = it },
                    )

                    SearchScreen(
                        innerPadding = innerPadding,
                        queryState = queryState,
                        uiState = uiState,
                        onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                        onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                        onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                        onLoadMore = { eventFlow.tryEmit(SearchScreenEvent.LoadMore) },
                        onBookClick = onBookClick,
                    )
                },
                suspenseFallback = {
                    val uiState = SearchPresenter(
                        eventFlow = eventFlow,
                        queryState = queryState,
                        currentQuery = currentQuery,
                        sortType = sortType,
                        searchResults = persistentListOf(),
                        hasNextPage = false,
                        onQueryChange = { currentQuery = it },
                        onSortChange = { sortType = it },
                    )

                    SearchScreen(
                        innerPadding = innerPadding,
                        queryState = queryState,
                        uiState = uiState,
                        onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                        onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                        onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                        onLoadMore = { eventFlow.tryEmit(SearchScreenEvent.LoadMore) },
                        onBookClick = onBookClick,
                    )
                }
            )
        ) { favoriteBookIds ->
            val uiState = SearchPresenter(
                eventFlow = eventFlow,
                queryState = queryState,
                currentQuery = currentQuery,
                sortType = sortType,
                searchResults = persistentListOf(),
                hasNextPage = false,
                onQueryChange = { currentQuery = it },
                onSortChange = { sortType = it },
            )

            SearchScreen(
                innerPadding = innerPadding,
                queryState = queryState,
                uiState = uiState,
                onSearchClick = { query -> eventFlow.tryEmit(SearchScreenEvent.Search(query)) },
                onClearClick = { eventFlow.tryEmit(SearchScreenEvent.ClearSearch) },
                onSortClick = { eventFlow.tryEmit(SearchScreenEvent.ToggleSort) },
                onLoadMore = { eventFlow.tryEmit(SearchScreenEvent.LoadMore) },
                onBookClick = onBookClick,
            )
        }
    }
}
