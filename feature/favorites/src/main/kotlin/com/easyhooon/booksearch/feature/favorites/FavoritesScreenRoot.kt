package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.SoilDataBoundary
import com.easyhooon.booksearch.core.common.SoilFallback
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesPresenter
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesScreenEvent
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberSubscription

@OptIn(ExperimentalSoilQueryApi::class)
context(context: FavoritesScreenContext)
@Composable
fun FavoritesScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }

    // UI 상태 추적을 위해 Root에서 관리
    var searchQuery by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(FavoritesSortType.LATEST) }
    var isPriceFilterEnabled by remember { mutableStateOf(false) }

    val eventFlow = rememberEventFlow<FavoritesScreenEvent>()

    SoilDataBoundary(
        state = rememberSubscription(
            key = context.createFavoriteBooksSubscriptionKey(
                query = searchQuery,
                sortType = sortType.value,
                isPriceFilterEnabled = isPriceFilterEnabled,
            )
        ),
        fallback = SoilFallback(
            errorFallback = {
                // 에러 시 빈 즐겨찾기로 표시
                val uiState = FavoritesPresenter(
                    eventFlow = eventFlow,
                    queryState = queryState,
                    searchQuery = searchQuery,
                    sortType = sortType,
                    isPriceFilterEnabled = isPriceFilterEnabled,
                    favoriteBooks = persistentListOf(),
                    onSearchQuery = { searchQuery = it },
                    onSortType = { sortType = it },
                    onPriceFilter = { isPriceFilterEnabled = it },
                )

                FavoritesScreen(
                    innerPadding = innerPadding,
                    queryState = queryState,
                    uiState = uiState,
                    onSearchClick = { eventFlow.tryEmit(FavoritesScreenEvent.Search) },
                    onClearClick = { eventFlow.tryEmit(FavoritesScreenEvent.ClearSearch) },
                    onFilterClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleFilter) },
                    onSortClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleSort) },
                    onFavoriteToggle = { book -> eventFlow.tryEmit(FavoritesScreenEvent.ToggleFavorite(book)) },
                    onBookClick = onBookClick,
                )
            },
            suspenseFallback = {
                // 로딩 중에도 화면 표시
                val uiState = FavoritesPresenter(
                    eventFlow = eventFlow,
                    queryState = queryState,
                    searchQuery = searchQuery,
                    sortType = sortType,
                    isPriceFilterEnabled = isPriceFilterEnabled,
                    favoriteBooks = persistentListOf(),
                    onSearchQuery = { searchQuery = it },
                    onSortType = { sortType = it },
                    onPriceFilter = { isPriceFilterEnabled = it },
                )

                FavoritesScreen(
                    innerPadding = innerPadding,
                    queryState = queryState,
                    uiState = uiState,
                    onSearchClick = { eventFlow.tryEmit(FavoritesScreenEvent.Search) },
                    onClearClick = { eventFlow.tryEmit(FavoritesScreenEvent.ClearSearch) },
                    onFilterClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleFilter) },
                    onSortClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleSort) },
                    onFavoriteToggle = { book -> eventFlow.tryEmit(FavoritesScreenEvent.ToggleFavorite(book)) },
                    onBookClick = onBookClick,
                )
            }
        )
    ) { favoriteBooks ->
        val uiState = FavoritesPresenter(
            eventFlow = eventFlow,
            queryState = queryState,
            searchQuery = searchQuery,
            sortType = sortType,
            isPriceFilterEnabled = isPriceFilterEnabled,
            favoriteBooks = favoriteBooks.toImmutableList(),
            onSearchQuery = { searchQuery = it },
            onSortType = { sortType = it },
            onPriceFilter = { isPriceFilterEnabled = it },
        )

        FavoritesScreen(
            innerPadding = innerPadding,
            queryState = queryState,
            uiState = uiState,
            onSearchClick = { eventFlow.tryEmit(FavoritesScreenEvent.Search) },
            onClearClick = { eventFlow.tryEmit(FavoritesScreenEvent.ClearSearch) },
            onFilterClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleFilter) },
            onSortClick = { eventFlow.tryEmit(FavoritesScreenEvent.ToggleSort) },
            onFavoriteToggle = { book -> eventFlow.tryEmit(FavoritesScreenEvent.ToggleFavorite(book)) },
            onBookClick = onBookClick,
        )
    }
}
