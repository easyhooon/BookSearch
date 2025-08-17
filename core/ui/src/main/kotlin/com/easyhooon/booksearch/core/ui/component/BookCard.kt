package com.easyhooon.booksearch.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.designsystem.ComponentPreview
import com.easyhooon.booksearch.core.designsystem.component.NetworkImage
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral400
import com.easyhooon.booksearch.core.designsystem.theme.Neutral600
import com.easyhooon.booksearch.core.designsystem.theme.White
import com.easyhooon.booksearch.core.designsystem.theme.body1SemiBold
import com.easyhooon.booksearch.core.designsystem.theme.heading2Bold
import com.easyhooon.booksearch.core.designsystem.theme.headline1Bold
import com.easyhooon.booksearch.core.designsystem.theme.label1Medium
import com.easyhooon.booksearch.core.designsystem.R as designR

@Composable
fun BookCard(
    book: BookUiModel,
    onBookClick: (BookUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .clickable { onBookClick(book) }
            .padding(8.dp),
    ) {
        Row {
            NetworkImage(
                imageUrl = book.thumbnail,
                contentDescription = "Book Thumbnail Image",
                modifier = Modifier
                    .width(100.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(size = 8.dp)),
                placeholder = painterResource(designR.drawable.ic_placeholder),
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(designR.string.book),
                    color = Neutral400,
                    style = body1SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = book.title,
                    color = Black,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = headline1Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Neutral600, fontWeight = FontWeight.Bold)) {
                            append(stringResource(designR.string.publisher_label))
                        }
                        withStyle(style = SpanStyle(color = Neutral400)) {
                            append(" ${book.publisher}")
                        }
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = label1Medium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Neutral600, fontWeight = FontWeight.Bold)) {
                            append(stringResource(designR.string.author_label))
                        }
                        withStyle(style = SpanStyle(color = Neutral400)) {
                            append(" ${book.authors.joinToString(separator = ", ")}")
                        }
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = label1Medium,
                )
            }
        }
        if (book.isFavorites) {
            Icon(
                imageVector = ImageVector.vectorResource(designR.drawable.ic_favorite_filled_red),
                contentDescription = "Favorited book",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd),
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(designR.drawable.ic_selected_favorites),
                contentDescription = "Not favorited book",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd),
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.End,
        ) {
            val originalPrice = book.price.toIntOrNull() ?: 0
            val salePrice = book.salePrice.toIntOrNull() ?: 0
            val hasDiscount = originalPrice > 0 && salePrice > 0 && originalPrice != salePrice

            if (hasDiscount) {
                val discountPercent = ((originalPrice - salePrice) * 100) / originalPrice

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$discountPercent%",
                        style = label1Medium,
                        color = com.easyhooon.booksearch.core.designsystem.theme.Red500,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${book.price}${stringResource(designR.string.won)}",
                        style = label1Medium,
                        color = Neutral400,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = "${if (book.salePrice == "-1") book.price else book.salePrice}${stringResource(designR.string.won)}",
                style = heading2Bold,
                color = Neutral600,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun BookItemPreview() {
    BookSearchTheme {
        BookCard(
            book = BookUiModel(
                title = "도서 제목",
                contents = "",
                url = "",
                isbn = "",
                datetime = "",
                authors = listOf("저자"),
                publisher = "출판사",
                translators = emptyList(),
                price = "",
                salePrice = "N",
                thumbnail = "",
                status = "",
            ),
            onBookClick = {},
        )
    }
}
