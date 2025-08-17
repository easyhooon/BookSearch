package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.core.designsystem.component.BookSearchTextField
import com.easyhooon.booksearch.core.designsystem.theme.Green500
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.ui.component.BookCard
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiAction
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiEvent
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiState
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesViewModel
import kotlinx.collections.immutable.ImmutableList
import com.easyhooon.booksearch.core.designsystem.R as designR

@Composable
internal fun FavoritesRoute(
    innerPadding: PaddingValues,
    navigateToDetail: (Book) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is FavoritesUiEvent.NavigateToDetail -> navigateToDetail(event.book)
        }
    }

    FavoritesScreen(
        innerPadding = innerPadding,
        uiState = uiState,
        favoriteBooks = favoriteBooks,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun FavoritesScreen(
    innerPadding: PaddingValues,
    uiState: FavoritesUiState,
    favoriteBooks: ImmutableList<Book>,
    onAction: (FavoritesUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        BookSearchTopAppBar(title = stringResource(id = designR.string.favorites_label))
        BookSearchTextField(
            queryState = uiState.queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = { query ->
                onAction(FavoritesUiAction.OnSearchClick)
            },
            onClear = {
                onAction(FavoritesUiAction.OnClearClick)
            },
            modifier = Modifier.padding(horizontal = 20.dp),
            borderStroke = BorderStroke(width = 1.dp, color = Green500),
            searchIconTint = Green500,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(
                count = favoriteBooks.size,
                key = { index -> favoriteBooks[index].isbn },
            ) { index ->
                BookCard(
                    book = favoriteBooks[index],
                    onBookClick = { book ->
                        onAction(FavoritesUiAction.OnBookClick(book))
                    },
                )
            }
        }
    }
}
