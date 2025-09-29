package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesPresenter
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesScreenEvent
import io.github.takahirom.rin.rememberRetained

context(context: FavoritesScreenContext)
@Composable
fun FavoritesScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    
    val eventFlow = rememberEventFlow<FavoritesScreenEvent>()

    val uiState = FavoritesPresenter(
        eventFlow = eventFlow,
        queryState = queryState,
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