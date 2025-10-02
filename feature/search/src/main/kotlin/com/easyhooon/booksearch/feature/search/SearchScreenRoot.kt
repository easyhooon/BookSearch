package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.SoilDataBoundary
import com.easyhooon.booksearch.core.common.SoilFallback
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.SearchBooksPageParam
import com.orhanobut.logger.Logger
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import com.easyhooon.booksearch.feature.search.presenter.SearchScreenEvent
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberInfiniteQuery

@OptIn(ExperimentalSoilQueryApi::class)
context(context: SearchScreenContext)
@Composable
fun SearchScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    val eventFlow = rememberEventFlow<SearchScreenEvent>()

    // 검색 쿼리와 정렬 상태 관리 - 화면 이동 후에도 유지
    var currentQuery by rememberRetained { mutableStateOf("") }
    var sortType by remember { mutableStateOf(SortType.ACCURACY) }

    // 즐겨찾기 구독 - Repository Flow 사용
    val favoriteBookIds by context.bookRepository.favoriteBooks
        .map { books -> books.map { it.isbn }.toSet() }
        .collectAsStateWithLifecycle(initialValue = emptySet())

    // 검색 쿼리가 있을 때만 InfiniteQuery 생성
    val searchInfiniteQuery = if (currentQuery.isNotEmpty()) {
        rememberInfiniteQuery(
            key = context.searchBooksQueryKey.create(
                query = currentQuery,
                sort = sortType.value,
                size = 20,
            ),
            select = { it }
        )
    } else null

    if (searchInfiniteQuery != null) {
        // 검색 결과가 있는 경우
        SoilDataBoundary(
            state = searchInfiniteQuery,
            fallback = SoilFallback(
                errorFallback = { ctx ->
                    SearchErrorContent(onRetry = { ctx.errorBoundaryContext.reset?.let { it() } })
                },
                suspenseFallback = {
                    SearchLoadingContent()
                }
            ),
        ) { searchData ->
            Logger.d("Combine emitted - favoriteBookIds: $favoriteBookIds, searchData size: ${searchData.size}")
            // Soil infinite query의 searchData에서 실제 데이터 추출
            val allSearchResults: List<BookUiModel> = searchData.flatMap { chunk -> chunk.data }

            // 검색 결과에 즐겨찾기 상태 반영
            val searchResultsWithFavorites = allSearchResults.map { book: BookUiModel ->
                book.copy(isFavorites = favoriteBookIds.contains(book.isbn))
            }.toImmutableList()
            Logger.d("SearchResults updated with favorites - total: ${searchResultsWithFavorites.size}, favorites: ${searchResultsWithFavorites.count { it.isFavorites == true }}")

            val uiState = SearchPresenter(
                eventFlow = eventFlow,
                queryState = queryState,
                currentQuery = currentQuery,
                sortType = sortType,
                searchResults = searchResultsWithFavorites,
                hasNextPage = searchInfiniteQuery.loadMoreParam != null,
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
                onBookClick = onBookClick,
                loadMore = { param ->
                    searchInfiniteQuery.loadMore.let { loadMoreFn ->
                        (param as? SearchBooksPageParam)?.let {
                            loadMoreFn(it)
                        }
                    } ?: Unit
                },
                loadMoreParam = searchInfiniteQuery.loadMoreParam,
            )
        }
    } else {
        // 검색 결과가 없는 경우
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
            onBookClick = onBookClick,
        )
    }
}
