package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.favorites.presenter.FavoritesPresenter
import io.github.takahirom.rin.rememberRetained

context(context: FavoritesScreenContext)
@Composable
fun FavoritesScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    
    val presenterState = FavoritesPresenter(
        queryState = queryState,
    )
    
    FavoritesScreen(
        innerPadding = innerPadding,
        queryState = queryState,
        uiState = presenterState.uiState,
        onAction = presenterState.onAction,
        onBookClick = onBookClick,
    )
}