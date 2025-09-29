package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.common.toast.ToastMessageEffect
import com.easyhooon.booksearch.feature.detail.presenter.DetailPresenter
import com.easyhooon.booksearch.feature.detail.presenter.DetailScreenEvent

context(context: DetailScreenContext)
@Composable
fun DetailScreenRoot(
    initialBook: BookUiModel,
    innerPadding: PaddingValues,
    onNavigateBack: () -> Unit,
) {
    val eventFlow = rememberEventFlow<DetailScreenEvent>()

    val uiState = DetailPresenter(
        initialBook = initialBook,
        eventFlow = eventFlow,
    )

    ToastMessageEffect(
        userMessageStateHolder = uiState.userMessageStateHolder,
    )

    DetailScreen(
        innerPadding = innerPadding,
        uiState = uiState,
        onFavoriteClick = { eventFlow.tryEmit(DetailScreenEvent.ToggleFavorite) },
        onBackClick = onNavigateBack,
    )
}
