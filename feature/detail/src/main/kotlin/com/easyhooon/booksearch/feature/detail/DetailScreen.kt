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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.common.extensions.toFormattedDate
import com.easyhooon.booksearch.core.common.extensions.toFormattedPrice
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.DevicePreview
import com.easyhooon.booksearch.core.designsystem.R as designR
import com.easyhooon.booksearch.core.designsystem.component.NetworkImage
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral100
import com.easyhooon.booksearch.core.designsystem.theme.Neutral600
import com.easyhooon.booksearch.core.designsystem.theme.body1Medium
import com.easyhooon.booksearch.core.designsystem.theme.heading1Bold
import com.easyhooon.booksearch.core.ui.component.BookSearchTopAppBar
import com.easyhooon.booksearch.core.common.toast.UserMessageStateHolderImpl
import com.easyhooon.booksearch.feature.detail.presenter.DetailUiState

@Composable
internal fun DetailScreen(
    innerPadding: PaddingValues,
    uiState: DetailUiState,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        BookSearchTopAppBar(
            startIconRes = designR.drawable.ic_chevron_left,
            startIconOnClick = onBackClick,
            endIconRes = if (uiState.book.isFavorites) designR.drawable.ic_favorite_filled_red
            else designR.drawable.ic_selected_favorites,
            endIconOnClick = onFavoriteClick,
        )
        DetailContent(book = uiState.book)
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
                Spacer(Modifier.height(4.dp))
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
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.publish_date_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append(book.datetime.toFormattedDate())
                        }
                    },
                    style = body1Medium,
                )
                Spacer(Modifier.height(4.dp))
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
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.price_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append("${book.price.toFormattedPrice()}${stringResource(R.string.won)}")
                        }
                    },
                    style = body1Medium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            append(stringResource(R.string.sale_price_label))
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append("${book.salePrice.toFormattedPrice()}${stringResource(R.string.won)}")
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
                .fillMaxWidth()
                .background(Neutral100, RoundedCornerShape(8.dp))
                .padding(16.dp),
        ) {
            Text(
                text = "${book.contents}...",
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
            uiState = DetailUiState(
                book = BookUiModel(),
                userMessageStateHolder = UserMessageStateHolderImpl(),
            ),
            onFavoriteClick = {},
            onBackClick = {},
        )
    }
}
