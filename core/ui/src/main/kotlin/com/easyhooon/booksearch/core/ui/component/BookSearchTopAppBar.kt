package com.easyhooon.booksearch.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral950
import com.easyhooon.booksearch.core.designsystem.theme.headline2SemiBold

@Composable
fun BookSearchTopAppBar(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    title: String = "",
    @DrawableRes startIconRes: Int? = null,
    startIconDescription: String = "",
    startIconOnClick: () -> Unit = {},
    @DrawableRes endIconRes: Int? = null,
    endIconDescription: String = "",
    endIconOnClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = if (isDark) Neutral950 else White)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (startIconRes != null) {
            IconButton(
                onClick = { startIconOnClick() },
            ) {
                Icon(
                    painter = painterResource(id = startIconRes),
                    contentDescription = startIconDescription,
                    tint = if (isDark) White else Color.Unspecified,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = headline2SemiBold,
        )

        if (endIconRes != null) {
            IconButton(
                onClick = { endIconOnClick() },
            ) {
                Icon(
                    painter = painterResource(id = endIconRes),
                    contentDescription = endIconDescription,
                    tint = Color.Unspecified,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Preview
@Composable
private fun BookSearchTopAppBarPreview() {
    BookSearchTheme {
        BookSearchTopAppBar(
            title = "title",
        )
    }
}
