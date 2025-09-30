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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.easyhooon.booksearch.feature.search.presenter.SearchUiState
import kotlinx.collections.immutable.ImmutableList
import soil.plant.compose.lazy.LazyLoad
import com.easyhooon.booksearch.core.designsystem.R as designR

enum class SortType(val value: String, val displayName: String) {
    ACCURACY("accuracy", "정확도순"),
    LATEST("latest", "최신순");

    fun toggle(): SortType = when (this) {
        ACCURACY -> LATEST
        LATEST -> ACCURACY
    }
}


@Composable
internal fun SearchScreen(
    innerPadding: PaddingValues,
    queryState: TextFieldState,
    uiState: SearchUiState,
    onSearchClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onSortClick: () -> Unit,
    onBookClick: (BookUiModel) -> Unit,
    loadMore: suspend (Any) -> Unit = {},
    loadMoreParam: Any? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Neutral100),
    ) {
        BookSearchTopAppBar(title = stringResource(designR.string.search_label))

        SearchHeader(
            queryState = queryState,
            sortLabel = uiState.sortType.displayName,
            onSearchClick = onSearchClick,
            onClearClick = onClearClick,
            onSortClick = onSortClick,
        )

        if (uiState.currentQuery.isNotEmpty()) {
            SearchContent(
                books = uiState.searchResults,
                hasNextPage = uiState.hasNextPage,
                onBookClick = onBookClick,
                loadMore = loadMore,
                loadMoreParam = loadMoreParam,
            )
        } else {
            SearchIdleContent()
        }
    }
}

@Composable
internal fun SearchHeader(
    queryState: TextFieldState,
    sortLabel: String,
    onSearchClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(White),
    ) {
        BookSearchTextField(
            queryState = queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = onSearchClick,
            onClear = onClearClick,
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
                onClick = onSortClick,
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
    hasNextPage: Boolean,
    onBookClick: (BookUiModel) -> Unit,
    loadMore: suspend (Any) -> Unit = {},
    loadMoreParam: Any? = null,
) {
    if (books.isEmpty()) {
        SearchEmptyContent()
    } else {
        val lazyListState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = books,
                key = { it.isbn },
            ) { book ->
                BookCard(
                    book = book,
                    onBookClick = { onBookClick(book) },
                )
            }

            // Loading indicator for next page
            if (books.isNotEmpty() && hasNextPage) {
                item(contentType = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Neutral500,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        LazyLoad(
            state = lazyListState,
            loadMore = { param -> loadMore(param) },
            loadMoreParam = loadMoreParam
        )
    }
}

@Composable
private fun SearchEmptyContent() {
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
}

@Composable
private fun SearchIdleContent() {
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

@Composable
internal fun SearchLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Neutral500)
    }
}

@Composable
internal fun SearchErrorContent(onRetry: () -> Unit) {
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
            onClick = onRetry,
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

@DevicePreview
@Composable
private fun SearchScreenPreview() {
    BookSearchTheme {
        SearchScreen(
            innerPadding = PaddingValues(),
            queryState = TextFieldState(),
            uiState = SearchUiState(),
            onSearchClick = {},
            onClearClick = {},
            onSortClick = {},
            onBookClick = {},
        )
    }
}
