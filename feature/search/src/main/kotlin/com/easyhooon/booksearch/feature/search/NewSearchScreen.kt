package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.DevicePreview
import com.easyhooon.booksearch.core.designsystem.R as designR
import com.easyhooon.booksearch.core.designsystem.component.BookSearchTextField
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral100
import com.easyhooon.booksearch.core.designsystem.theme.Neutral200
import com.easyhooon.booksearch.core.designsystem.theme.Neutral500
import com.easyhooon.booksearch.core.designsystem.theme.White
import com.easyhooon.booksearch.core.designsystem.theme.body1Medium
import com.easyhooon.booksearch.core.designsystem.theme.body1SemiBold
import com.easyhooon.booksearch.core.ui.component.BookCard
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.feature.search.component.InfinityLazyColumn
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import com.easyhooon.booksearch.feature.search.presenter.SearchState
import com.easyhooon.booksearch.feature.search.presenter.SearchUiAction
import io.github.takahirom.rin.rememberRetained

@Composable
internal fun NewSearchRoute(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }

    val presenterState = SearchPresenter(
        queryState = queryState,
        onNavigateToDetail = navigateToDetail
    )

    NewSearchScreen(
        innerPadding = innerPadding,
        uiState = presenterState.uiState,
        onAction = presenterState.onAction
    )
}

@Composable
internal fun NewSearchScreen(
    innerPadding: PaddingValues,
    uiState: com.easyhooon.booksearch.feature.search.presenter.SearchUiState,
    onAction: (SearchUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Neutral100),
    ) {
        BookSearchTopAppBar(title = stringResource(designR.string.search_label))
        NewSearchHeader(
            queryState = uiState.queryState,
            sortLabel = uiState.sortType.displayName,
            onAction = onAction,
        )
        NewSearchContent(
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Composable
internal fun NewSearchHeader(
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
internal fun NewSearchContent(
    uiState: com.easyhooon.booksearch.feature.search.presenter.SearchUiState,
    onAction: (SearchUiAction) -> Unit,
) {
    when (uiState.searchState) {
        is SearchState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Neutral500)
            }
        }

        is SearchState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.search_error),
                    color = Black,
                    style = body1Medium,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedButton(
                    onClick = { onAction(SearchUiAction.OnRetryClick) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = White,
                        contentColor = Black,
                    ),
                    border = BorderStroke(width = 1.dp, color = Neutral200),
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        color = Black,
                        style = body1SemiBold,
                    )
                }
            }
        }

        is SearchState.Success -> {
            if (uiState.books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.empty_results),
                        color = Black,
                        style = body1Medium,
                    )
                }
            } else {
                InfinityLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
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

                    if (!uiState.isLastPage) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = Neutral500)
                            }
                        }
                    }
                }
            }
        }

        is SearchState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.search_idle),
                    color = Black,
                    style = body1Medium,
                )
            }
        }
    }
}

@DevicePreview
@Composable
private fun NewSearchScreenPreview() {
    BookSearchTheme {
        // Preview implementation would go here
    }
}
