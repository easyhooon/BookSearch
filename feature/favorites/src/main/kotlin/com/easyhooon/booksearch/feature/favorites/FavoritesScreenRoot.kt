package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesPresenter
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesScreenEvent
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberSubscription
import soil.query.core.getOrNull

@OptIn(ExperimentalSoilQueryApi::class)
context(context: FavoritesScreenContext)
@Composable
fun FavoritesScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.TITLE_ASC) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }

    val eventFlow = rememberEventFlow<FavoritesScreenEvent>()

    val subscriptionState = rememberSubscription(
        key = context.createFavoriteBooksSubscriptionKey(
            query = searchQuery,
            sortType = sortType.value,
            isPriceFilterEnabled = isPriceFilterEnabled,
        )
    )

    val favoriteBooks = subscriptionState.reply.getOrNull() ?: emptyList()

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
