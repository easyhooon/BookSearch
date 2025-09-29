package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import io.github.takahirom.rin.rememberRetained

context(context: SearchScreenContext)
@Composable
fun SearchScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    
    val presenterState = SearchPresenter(
        queryState = queryState,
    )
    
    SearchScreen(
        innerPadding = innerPadding,
        queryState = queryState,
        uiState = presenterState.uiState,
        onAction = presenterState.onAction,
        onBookClick = onBookClick,
    )
}