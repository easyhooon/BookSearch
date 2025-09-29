package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.SoilDataBoundary
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import com.easyhooon.booksearch.feature.search.presenter.SearchScreenEvent
import io.github.takahirom.rin.rememberRetained
import soil.query.annotation.ExperimentalSoilQueryApi
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

    SoilDataBoundary(
        state = rememberSubscription(
            key = context.createFavoriteBookIdsSubscriptionKey()
        ),
        fallback = com.easyhooon.booksearch.core.common.SoilFallback(
            errorFallback = {
                // 에러 시 빈 즐겨찾기로 표시
                val uiState = SearchPresenter(
                    eventFlow = eventFlow,
                    queryState = queryState,
                    favoriteBookIds = emptySet(),
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
                // 로딩 중에도 화면 표시
                val uiState = SearchPresenter(
                    eventFlow = eventFlow,
                    queryState = queryState,
                    favoriteBookIds = emptySet(),
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
            favoriteBookIds = favoriteBookIds,
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
