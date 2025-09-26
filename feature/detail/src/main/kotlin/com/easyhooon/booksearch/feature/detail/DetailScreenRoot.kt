package com.easyhooon.booksearch.feature.detail

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.detail.presenter.DetailPresenter
import com.easyhooon.booksearch.feature.detail.presenter.DetailUiEvent

context(context: DetailScreenContext)
@Composable
fun DetailScreenRoot(
    initialBook: BookUiModel,
    innerPadding: PaddingValues,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val eventFlow = rememberEventFlow<DetailUiEvent>()

    EventEffect(eventFlow) { event ->
        when (event) {
            is DetailUiEvent.NavigateBack -> {
                onNavigateBack()
            }
            is DetailUiEvent.ShowToast -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val presenterState = DetailPresenter(
        initialBook = initialBook,
        eventFlow = eventFlow,
    )

    DetailScreen(
        innerPadding = innerPadding,
        uiState = presenterState.uiState,
        onAction = presenterState.onAction,
    )
}
