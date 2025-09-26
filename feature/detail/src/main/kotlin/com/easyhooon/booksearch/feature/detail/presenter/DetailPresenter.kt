package com.easyhooon.booksearch.feature.detail.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.feature.detail.DetailScreenContext
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import soil.query.compose.rememberMutation

data class DetailUiState(
    val book: BookUiModel,
)

sealed interface DetailUiAction {
    data object OnBackClick : DetailUiAction
    data object OnFavoriteClick : DetailUiAction
}

sealed interface DetailUiEvent {
    data object NavigateBack : DetailUiEvent
    data class ShowToast(val message: String) : DetailUiEvent
}

data class DetailPresenterState(
    val uiState: DetailUiState,
    val onAction: (DetailUiAction) -> Unit,
)

context(context: DetailScreenContext)
@Composable
fun DetailPresenter(
    initialBook: BookUiModel,
    eventFlow: MutableSharedFlow<DetailUiEvent>,
): DetailPresenterState = providePresenterDefaults {
    var currentBook by rememberRetained { mutableStateOf(initialBook) }
    val coroutineScope = rememberCoroutineScope()


    // 즐겨찾기 토글 Mutation
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }


    val onAction: (DetailUiAction) -> Unit = remember(currentBook) {
        { action ->
            when (action) {
                is DetailUiAction.OnBackClick -> {
                    eventFlow.tryEmit(DetailUiEvent.NavigateBack)
                }
                is DetailUiAction.OnFavoriteClick -> {
                    // Optimistic update
                    val newFavoriteStatus = !currentBook.isFavorites
                    currentBook = currentBook.copy(isFavorites = newFavoriteStatus)
                    
                    // Room 데이터베이스에 실제 변경사항 적용 (비동기)
                    coroutineScope.launch {
                        toggleMutationKey = context.createToggleFavoriteQueryKey(currentBook)
                        toggleFavoriteMutation?.mutateAsync(Unit)
                    }
                    
                    val message = if (newFavoriteStatus) {
                        "즐겨찾기에 추가되었습니다"
                    } else {
                        "즐겨찾기에서 삭제되었습니다"
                    }
                    eventFlow.tryEmit(DetailUiEvent.ShowToast(message))
                }
            }
        }
    }

    val uiState = DetailUiState(
        book = currentBook,
    )

    return DetailPresenterState(
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
private inline fun providePresenterDefaults(
    content: @Composable () -> DetailPresenterState
): DetailPresenterState = content()