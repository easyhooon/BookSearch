package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.component.BookSearchTextField
import com.easyhooon.booksearch.core.designsystem.theme.Green500
import com.easyhooon.booksearch.core.ui.component.BookCard
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.core.ui.component.InfinityLazyColumn
import com.easyhooon.booksearch.core.ui.component.LoadStateFooter
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiAction
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiEvent
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiState
import com.easyhooon.booksearch.feature.search.viewmodel.SearchViewModel
import com.easyhooon.booksearch.core.designsystem.R as designR

@Composable
internal fun SearchRoute(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is SearchUiEvent.NavigateToDetail -> navigateToDetail(event.book)
        }
    }

    SearchScreen(
        innerPadding = innerPadding,
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun SearchScreen(
    innerPadding: PaddingValues,
    uiState: SearchUiState,
    onAction: (SearchUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        BookSearchTopAppBar(title = stringResource(designR.string.search_label))
        BookSearchTextField(
            queryState = uiState.queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = { query ->
                onAction(SearchUiAction.OnSearchClick(query))
            },
            onClear = {
                onAction(SearchUiAction.OnClearClick)
            },
            modifier = Modifier.padding(horizontal = 20.dp),
            borderStroke = BorderStroke(width = 1.dp, color = Green500),
            searchIconTint = Green500,
        )
        InfinityLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            loadMore = { onAction(SearchUiAction.OnLoadMore) },
        ) {
            items(
                items = uiState.books,
                key = { it.isbn },
            ) { book ->
                BookCard(
                    book = book,
                    onBookClick = { onAction(SearchUiAction.OnBookClick(book)) },
                )
            }

            item {
                LoadStateFooter(
                    footerState = uiState.footerState,
                    onRetryClick = { onAction(SearchUiAction.OnRetryClick) },
                )
            }
        }
    }
}
