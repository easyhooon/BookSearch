package com.easyhooon.booksearch.feature.detail.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.detail.DetailScreenContext
import com.orhanobut.logger.Logger
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

    // 즐겨찾기 토글 Mutation - 현재 책으로 미리 생성
    val toggleFavoriteMutation = rememberMutation(
        key = context.createToggleFavoriteQueryKey(currentBook)
    )

    val onAction: (DetailUiAction) -> Unit = remember(currentBook) {
        { action ->
            when (action) {
                is DetailUiAction.OnBackClick -> {
                    eventFlow.tryEmit(DetailUiEvent.NavigateBack)
                }

                is DetailUiAction.OnFavoriteClick -> {
                    // 수동 Optimistic update
                    val newFavoriteStatus = !currentBook.isFavorites
                    val updatedBook = currentBook.copy(isFavorites = newFavoriteStatus)

                    Logger.d("DetailPresenter - OnFavoriteClick: ${currentBook.title}, current: ${currentBook.isFavorites}, new: $newFavoriteStatus")

                    // 즉시 UI 업데이트
                    currentBook = updatedBook

                    // 실제 mutation 실행
                    coroutineScope.launch {
                        try {
                            Logger.d("DetailPresenter - executing mutation")

                            val result = toggleFavoriteMutation.mutateAsync(Unit)
                            Logger.d("DetailPresenter - mutation executed, result: $result")

                            val message = if (newFavoriteStatus) {
                                "즐겨찾기에 추가되었습니다"
                            } else {
                                "즐겨찾기에서 삭제되었습니다"
                            }
                            eventFlow.tryEmit(DetailUiEvent.ShowToast(message))
                        } catch (e: Throwable) {
                            // 실패 시 rollback
                            currentBook = currentBook.copy(isFavorites = !newFavoriteStatus)
                            Logger.e(e, "Failed to toggle favorite status")
                            eventFlow.tryEmit(DetailUiEvent.ShowToast("즐겨찾기 변경에 실패했습니다"))
                        }
                    }
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
    content: @Composable () -> DetailPresenterState,
): DetailPresenterState = content()
