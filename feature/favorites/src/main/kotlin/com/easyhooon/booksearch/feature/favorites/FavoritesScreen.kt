package com.easyhooon.booksearch.feature.favorites

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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiAction
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiEvent
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiState
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.easyhooon.booksearch.core.designsystem.R as designR

@Composable
internal fun FavoritesRoute(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
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
    favoriteBooks: ImmutableList<BookUiModel>,
    onAction: (FavoritesUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Neutral100),
    ) {
        BookSearchTopAppBar(title = stringResource(id = designR.string.favorites_label))
        FavoritesHeader(
            queryState = uiState.queryState,
            sortLabel = uiState.sortType.label,
            onAction = onAction,
        )
        FavoritesContent(
            favoriteBooks = favoriteBooks,
            onAction = onAction,
        )
    }
}

@Composable
internal fun FavoritesHeader(
    queryState: TextFieldState,
    sortLabel: String,
    onAction: (FavoritesUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(White),
    ) {
        BookSearchTextField(
            queryState = queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = { query ->
                onAction(FavoritesUiAction.OnSearchClick)
            },
            onClear = {
                onAction(FavoritesUiAction.OnClearClick)
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
            Row {
                OutlinedButton(
                    onClick = { onAction(FavoritesUiAction.OnSortClick) },
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
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedButton(
                    onClick = { onAction(FavoritesUiAction.OnFilterClick) },
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
                            imageVector = ImageVector.vectorResource(designR.drawable.ic_filter),
                            contentDescription = "Filter Icon",
                            tint = Color.Unspecified,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(designR.string.filter_label),
                            color = Black,
                            style = body1SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun FavoritesContent(
    favoriteBooks: ImmutableList<BookUiModel>,
    onAction: (FavoritesUiAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

@DevicePreview
@Composable
private fun FavoritesScreenPreview() {
    BookSearchTheme {
        FavoritesScreen(
            innerPadding = PaddingValues(),
            uiState = FavoritesUiState(),
            favoriteBooks = persistentListOf(),
            onAction = {},
        )
    }
}
