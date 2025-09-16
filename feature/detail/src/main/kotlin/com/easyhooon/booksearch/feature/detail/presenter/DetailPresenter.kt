package com.easyhooon.booksearch.feature.detail.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.UiText
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.detail.R
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiAction
import io.github.takahirom.rin.rememberRetained

data class DetailUiState(
    val book: BookUiModel = BookUiModel(),
)

sealed interface DetailUiEvent {
    data object NavigateBack : DetailUiEvent
    data class ShowToast(val message: UiText) : DetailUiEvent
}

data class DetailPresenterState(
    val uiState: DetailUiState,
    val onAction: (DetailUiAction) -> Unit,
)

@Composable
fun DetailPresenter(
    book: BookUiModel,
    onNavigateBack: () -> Unit,
    onShowToast: (UiText) -> Unit,
): DetailPresenterState {
    var currentBook by rememberRetained { mutableStateOf(book) }
    
    val eventFlow = rememberEventFlow<DetailUiEvent>()
    
    EventEffect(eventFlow) { event ->
        when (event) {
            is DetailUiEvent.NavigateBack -> {
                onNavigateBack()
            }
            is DetailUiEvent.ShowToast -> {
                onShowToast(event.message)
            }
        }
    }
    
    val onAction: (DetailUiAction) -> Unit = remember {
        { action ->
            when (action) {
                is DetailUiAction.OnBackClick -> {
                    eventFlow.tryEmit(DetailUiEvent.NavigateBack)
                }
                is DetailUiAction.OnFavoritesClick -> {
                    // Simple UI state toggle - no mutation needed per user feedback
                    val newFavoriteStatus = !currentBook.isFavorites
                    currentBook = currentBook.copy(isFavorites = newFavoriteStatus)
                    
                    val message = if (newFavoriteStatus) {
                        UiText.StringResource(R.string.insert_favorites)
                    } else {
                        UiText.StringResource(R.string.delete_favorites)
                    }
                    eventFlow.tryEmit(DetailUiEvent.ShowToast(message))
                }
            }
        }
    }
    
    val uiState = DetailUiState(
        book = currentBook
    )
    
    return DetailPresenterState(
        uiState = uiState,
        onAction = onAction,
    )
}