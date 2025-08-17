package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.DevicePreview
import com.easyhooon.booksearch.core.designsystem.component.NetworkImage
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral100
import com.easyhooon.booksearch.core.designsystem.theme.Neutral600
import com.easyhooon.booksearch.core.designsystem.theme.body1Medium
import com.easyhooon.booksearch.core.designsystem.theme.heading1Bold
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiAction
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiEvent
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiState
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailViewModel
import com.easyhooon.booksearch.core.designsystem.R as designR

@Composable
internal fun DetailRoute(
    innerPadding: PaddingValues,
    popBackStack: () -> Unit,
    book: BookUiModel,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is DetailUiEvent.NavigateBack -> popBackStack()
        }
    }

    DetailScreen(
        innerPadding = innerPadding,
        uiState = uiState,
        book = book,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun DetailScreen(
    innerPadding: PaddingValues,
    uiState: DetailUiState,
    book: BookUiModel,
    onAction: (DetailUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        BookSearchTopAppBar(
            startIconRes = designR.drawable.ic_chevron_left,
            startIconOnClick = {
                onAction(DetailUiAction.OnBackClick)
            },
            endIconRes = if (uiState.isFavorite) designR.drawable.ic_favorite_filled_red
            else designR.drawable.ic_selected_favorites,
            endIconOnClick = {
                onAction(DetailUiAction.OnFavoritesClick(book))
            },
        )
        DetailContent(book = book)
    }
}

@Composable
internal fun DetailContent(
    book: BookUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = book.title,
            modifier = Modifier.fillMaxWidth(),
            color = Black,
            style = heading1Bold,
        )
        Spacer(Modifier.height(16.dp))
        Row {
            NetworkImage(
                imageUrl = book.thumbnail,
                contentDescription = "Book Thumbnail Image",
                modifier = Modifier
                    .width(102.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(size = 8.dp)),
                placeholder = painterResource(designR.drawable.ic_placeholder),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.author_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append(book.authors.joinToString(", "))
                        }
                    },
                    style = body1Medium,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.publisher_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append(book.publisher)
                        }
                    },
                    style = body1Medium,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.publish_date_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append(book.datetime)
                        }
                    },
                    style = body1Medium,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.isbn_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append(book.isbn)
                        }
                    },
                    style = body1Medium,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.price_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append("${book.price}${stringResource(R.string.won)}")
                        }
                    },
                    style = body1Medium,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.sale_price_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append("${book.salePrice}${stringResource(R.string.won)}")
                        }
                    },
                    style = body1Medium,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.book_introduce),
            modifier = Modifier.fillMaxWidth(),
            color = Black,
            style = heading1Bold,
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .background(Neutral100)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Text(
                text = book.contents,
                modifier = Modifier.fillMaxWidth(),
                color = Neutral600,
                style = body1Medium,
            )
        }
    }
}

@DevicePreview
@Composable
private fun DetailScreenPreview() {
    BookSearchTheme {
        DetailScreen(
            innerPadding = PaddingValues(),
            uiState = DetailUiState(),
            book = BookUiModel(
                title = "도서 제목",
                contents = "책 소개",
                url = "",
                isbn = "ISBN",
                datetime = "출간일",
                authors = listOf("저자"),
                publisher = "출판사",
                translators = listOf("번역가"),
                price = "N",
                salePrice = "N",
                thumbnail = "https://example.com/thumbnail.jpg",
                status = "",
            ),
            onAction = {},
        )
    }
}
