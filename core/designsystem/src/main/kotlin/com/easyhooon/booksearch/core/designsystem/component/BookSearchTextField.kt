package com.easyhooon.booksearch.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.designsystem.ComponentPreview
import com.easyhooon.booksearch.core.designsystem.R
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.easyhooon.booksearch.core.designsystem.theme.Neutral400
import com.easyhooon.booksearch.core.designsystem.theme.Neutral500
import com.easyhooon.booksearch.core.designsystem.theme.Neutral50
import com.easyhooon.booksearch.core.designsystem.theme.Neutral800
import com.easyhooon.booksearch.core.designsystem.theme.body2Medium
import com.easyhooon.booksearch.core.designsystem.theme.body2Regular

val bookSearchTextSelectionColors = TextSelectionColors(
    handleColor = Neutral500,
    backgroundColor = Neutral500,
)

@Composable
fun BookSearchTextField(
    queryState: TextFieldState,
    @StringRes queryHintRes: Int,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Neutral50,
    textColor: Color = Neutral800,
    cornerShape: RoundedCornerShape = RoundedCornerShape(8.dp),
    borderStroke: BorderStroke? = null,
    searchIconTint: Color = Neutral800,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(LocalTextSelectionColors provides bookSearchTextSelectionColors) {
        BasicTextField(
            state = queryState,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            textStyle = body2Medium.copy(color = textColor),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            onKeyboardAction = {
                onSearch(queryState.text.toString())
                keyboardController?.hide()
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            decorator = { innerTextField ->
                Row(
                    modifier = modifier
                        .background(color = backgroundColor, shape = cornerShape)
                        .then(
                            if (borderStroke != null) {
                                Modifier.border(borderStroke, shape = cornerShape)
                            } else {
                                Modifier
                            },
                        )
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (queryState.text.isEmpty()) {
                            Text(
                                text = stringResource(id = queryHintRes),
                                color = Neutral400,
                                style = body2Regular,
                            )
                        }
                        innerTextField()
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (queryState.text.toString().isNotEmpty()) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_x_circle),
                            contentDescription = "Clear Icon",
                            modifier = Modifier.clickable {
                                onClear()
                            },
                            tint = Color.Unspecified,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = "Search Icon",
                        modifier = Modifier.clickable {
                            onSearch(queryState.text.toString())
                            keyboardController?.hide()
                        },
                        tint = searchIconTint,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            },
        )
    }
}

@ComponentPreview
@Composable
private fun bookSearchTextFieldPreview() {
    BookSearchTheme {
        BookSearchTextField(
            queryState = TextFieldState("검색"),
            queryHintRes = R.string.search_book_hint,
            onSearch = {},
            onClear = {},
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
    }
}
