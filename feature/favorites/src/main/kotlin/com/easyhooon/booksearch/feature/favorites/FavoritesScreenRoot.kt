package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.mapper.toUiModel
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesPresenter
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesScreenEvent
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi

@OptIn(ExperimentalSoilQueryApi::class)
context(context: FavoritesScreenContext)
@Composable
fun FavoritesScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberTextFieldState()
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.TITLE_ASC) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }

    val eventFlow = rememberEventFlow<FavoritesScreenEvent>()

    val allFavoriteBooks by context.bookRepository.favoriteBooks
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val favoriteBooks by remember(searchQuery, sortType, isPriceFilterEnabled) {
        derivedStateOf {
            allFavoriteBooks
                .filter { book ->
                    if (searchQuery.isNotEmpty()) {
                        book.title.contains(searchQuery, ignoreCase = true)
                    } else {
                        true
                    }
                }
                .filter { book ->
                    // 가격 필터링
                    if (isPriceFilterEnabled) {
                        book.price.isNotEmpty() && book.price != "0"
                    } else {
                        true
                    }
                }
                .let { filteredBooks ->
                    // 정렬
                    when (sortType) {
                        FavoritesSortType.TITLE_ASC -> filteredBooks.sortedBy { it.title }
                        FavoritesSortType.TITLE_DESC -> filteredBooks.sortedByDescending { it.title }
                    }
                }
                .map { it.toUiModel().copy(isFavorites = true) }
        }
    }

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
        onSearchClick = {
            searchQuery = queryState.text.toString()
        },
        onClearClick = {
            queryState.clearText()
            searchQuery = ""
        },
        onFilterClick = {
            isPriceFilterEnabled = !isPriceFilterEnabled
        },
        onSortClick = {
            sortType = sortType.next()
        },
        onFavoriteToggle = { book -> eventFlow.tryEmit(FavoritesScreenEvent.ToggleFavorite(book)) },
        onBookClick = onBookClick,
    )
}
