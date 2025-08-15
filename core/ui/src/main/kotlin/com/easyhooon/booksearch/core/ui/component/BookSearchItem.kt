package com.easyhooon.booksearch.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.designsystem.ComponentPreview
import com.easyhooon.booksearch.core.designsystem.R
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral500
import com.easyhooon.booksearch.core.designsystem.theme.body1Medium

@Composable
fun BookSearchItem(
    query: String,
    onQueryClick: (String) -> Unit,
    onRemoveIconClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onQueryClick(query) }
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = query,
            color = Neutral500,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = body1Medium,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
            contentDescription = "Remove Icon",
            tint = Neutral500,
            modifier = Modifier
                .size(18.dp)
                .clickable { onRemoveIconClick(query) },
        )
    }
}

@ComponentPreview
@Composable
private fun SearchItemPreview() {
    BookSearchTheme {
        BookSearchItem(
            query = "검색어 검색어 검색어 검색어 검색어",
            onQueryClick = {},
            onRemoveIconClick = {},
        )
    }
}
