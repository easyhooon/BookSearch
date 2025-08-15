package com.easyhooon.booksearch.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.easyhooon.booksearch.core.designsystem.ComponentPreview
import com.easyhooon.booksearch.core.designsystem.R
import com.easyhooon.booksearch.core.designsystem.theme.BookSearchTheme
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@Composable
fun NetworkImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    placeholder: Painter? = painterResource(R.drawable.ic_placeholder),
    contentScale: ContentScale = ContentScale.Crop,
) {
    CoilImage(
        imageModel = { imageUrl },
        modifier = modifier,
        component = rememberImageComponent {
            +PlaceholderPlugin.Loading(placeholder)
            +PlaceholderPlugin.Failure(placeholder)
        },
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center,
            contentDescription = contentDescription,
        ),
        previewPlaceholder = placeholder,
    )
}

@ComponentPreview
@Composable
private fun NetworkImagePreview() {
    BookSearchTheme {
        NetworkImage(
            imageUrl = "",
            contentDescription = "",
            placeholder = painterResource(R.drawable.ic_placeholder),
        )
    }
}
