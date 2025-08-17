package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.DevicePreview
import com.easyhooon.booksearch.core.designsystem.component.BookSearchTextField
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral100
import com.easyhooon.booksearch.core.designsystem.theme.Neutral200
import com.easyhooon.booksearch.core.designsystem.theme.Neutral500
import com.easyhooon.booksearch.core.designsystem.theme.White
import com.easyhooon.booksearch.core.designsystem.theme.body1SemiBold
import com.easyhooon.booksearch.core.ui.component.BookCard
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.core.ui.component.FooterState
import com.easyhooon.booksearch.core.ui.component.InfinityLazyColumn
import com.easyhooon.booksearch.core.ui.component.LoadStateFooter
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiAction
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiEvent
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiState
import com.easyhooon.booksearch.feature.search.viewmodel.SearchViewModel
import kotlinx.collections.immutable.ImmutableList
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
            .padding(innerPadding)
            .background(Neutral100),
    ) {
        BookSearchTopAppBar(title = stringResource(designR.string.search_label))
        SearchHeader(
            queryState = uiState.queryState,
            sortLabel = uiState.sortType.label,
            onAction = onAction,
        )
        SearchContent(
            books = uiState.books,
            footerState = uiState.footerState,
            onAction = onAction,
        )
    }
}

@Composable
internal fun SearchHeader(
    queryState: TextFieldState,
    sortLabel: String,
    onAction: (SearchUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(White),
    ) {
        BookSearchTextField(
            queryState = queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = { query ->
                onAction(SearchUiAction.OnSearchClick(query))
            },
            onClear = {
                onAction(SearchUiAction.OnClearClick)
            },
            modifier = Modifier.padding(horizontal = 20.dp),
            borderStroke = BorderStroke(width = 1.dp, color = Neutral500),
            searchIconTint = Neutral500,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = sortLabel,
                color = Black,
                style = body1SemiBold,
            )
            OutlinedButton(
                onClick = { onAction(SearchUiAction.OnSortClick) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor = Black,
                ),
                border = BorderStroke(width = 1.dp, color = Neutral200),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(designR.drawable.ic_sort),
                        contentDescription = "Sort Icon",
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(designR.string.sort_label),
                        color = Black,
                        style = body1SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun SearchContent(
    books: ImmutableList<BookUiModel>,
    footerState: FooterState,
    onAction: (SearchUiAction) -> Unit,
) {
    InfinityLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        loadMore = { onAction(SearchUiAction.OnLoadMore) },
    ) {
        items(
            items = books,
            key = { it.isbn },
        ) { book ->
            BookCard(
                book = book,
                onBookClick = { onAction(SearchUiAction.OnBookClick(book)) },
            )
        }

        item {
            LoadStateFooter(
                footerState = footerState,
                onRetryClick = { onAction(SearchUiAction.OnRetryClick) },
            )
        }
    }
}

@DevicePreview
@Composable
private fun SearchScreenPreview() {
    BookSearchTheme {
        SearchScreen(
            innerPadding = PaddingValues(),
            uiState = SearchUiState(),
            onAction = {},
        )
    }
}
