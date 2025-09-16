package com.easyhooon.booksearch.feature.favorites

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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.GetFavoriteBooksQueryKey
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
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import soil.plant.compose.reacty.ErrorBoundary
import soil.plant.compose.reacty.Suspense
import soil.query.compose.rememberQuery
import com.easyhooon.booksearch.core.designsystem.R as designR

enum class FavoritesSortType(val value: String, val label: String) {
    LATEST("LATEST", "최신순"),
    OLDEST("OLDEST", "오래된순"),
    PRICE_LOW_TO_HIGH("PRICE_LOW_TO_HIGH", "가격 낮은순"),
    PRICE_HIGH_TO_LOW("PRICE_HIGH_TO_LOW", "가격 높은순");
    
    fun next(): FavoritesSortType = when (this) {
        LATEST -> OLDEST
        OLDEST -> PRICE_LOW_TO_HIGH  
        PRICE_LOW_TO_HIGH -> PRICE_HIGH_TO_LOW
        PRICE_HIGH_TO_LOW -> LATEST
    }
}

@Composable
internal fun FavoritesRoute(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }
    
    SuspenseFavoritesScreen(
        innerPadding = innerPadding,
        queryState = queryState,
        onNavigateToDetail = navigateToDetail,
    )
}

@Composable
internal fun SuspenseFavoritesScreen(
    innerPadding: PaddingValues,
    queryState: TextFieldState,
    onNavigateToDetail: (BookUiModel) -> Unit,
) {
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.LATEST) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Neutral100),
    ) {
        BookSearchTopAppBar(title = stringResource(id = designR.string.favorites_label))
        
        SuspenseFavoritesHeader(
            queryState = queryState,
            sortLabel = sortType.label,
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
        )
        
        ErrorBoundary(
            fallback = { context ->
                SuspenseFavoritesErrorContent(onRetry = { context.reset?.invoke() })
            }
        ) {
            Suspense(
                fallback = {
                    SuspenseFavoritesLoadingContent()
                }
            ) {
                SuspenseFavoritesContent(
                    query = searchQuery,
                    sortType = sortType,
                    isPriceFilterEnabled = isPriceFilterEnabled,
                    onNavigateToDetail = onNavigateToDetail,
                )
            }
        }
    }
}

@Composable
internal fun SuspenseFavoritesHeader(
    queryState: TextFieldState,
    sortLabel: String,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(White),
    ) {
        BookSearchTextField(
            queryState = queryState,
            queryHintRes = designR.string.search_book_hint,
            onSearch = { onSearchClick() },
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
            Row {
                OutlinedButton(
                    onClick = onFilterClick,
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
                Spacer(modifier = Modifier.width(4.dp))
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
}

@Composable
internal fun SuspenseFavoritesContent(
    query: String,
    sortType: FavoritesSortType,
    isPriceFilterEnabled: Boolean,
    onNavigateToDetail: (BookUiModel) -> Unit,
) {
    val favoritesQuery = rememberQuery<List<BookUiModel>>(
        key = GetFavoriteBooksQueryKey(
            query = query,
            sortType = sortType.value,
            isPriceFilterEnabled = isPriceFilterEnabled,
        )
    )
    
    // We'll handle toggle favorite in each BookCard individually
    
    val books = favoritesQuery.data?.toImmutableList() ?: persistentListOf()
    
    if (books.isEmpty()) {
        SuspenseFavoritesEmptyContent()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                count = books.size,
                key = { index -> books[index].isbn },
            ) { index ->
                BookCard(
                    book = books[index],
                    onBookClick = { book ->
                        onNavigateToDetail(book)
                    },
                    isPriceFilterEnabled = isPriceFilterEnabled,
                    onFavoritesClick = { book ->
                        // TODO: Handle favorite toggle
                    },
                )
            }
        }
    }
}

@Composable
private fun SuspenseFavoritesLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Neutral500)
    }
}

@Composable
private fun SuspenseFavoritesErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "에러가 발생했습니다",
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
                text = "다시 시도",
                color = Black,
                style = body1SemiBold,
            )
        }
    }
}

@Composable
private fun SuspenseFavoritesEmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.empty_favorites),
            color = Black,
            style = body1Medium,
        )
    }
}

@DevicePreview
@Composable
private fun SuspenseFavoritesScreenPreview() {
    BookSearchTheme {
        SuspenseFavoritesScreen(
            innerPadding = PaddingValues(),
            queryState = TextFieldState(),
            onNavigateToDetail = {},
        )
    }
}