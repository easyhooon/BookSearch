package com.easyhooon.booksearch.feature.detail.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.toast.UserMessageStateHolder
import com.easyhooon.booksearch.core.common.toast.UserMessageStateHolderImpl
import com.easyhooon.booksearch.feature.detail.DetailScreenContext
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.MutableSharedFlow
import soil.query.compose.rememberMutation

data class DetailUiState(
    val book: BookUiModel,
    val userMessageStateHolder: UserMessageStateHolder,
)

sealed interface DetailUiAction {
    data object OnFavoriteClick : DetailUiAction
}

sealed interface DetailUiEvent {
    data object FavoriteToggle : DetailUiEvent
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
    val userMessageStateHolder = remember { UserMessageStateHolderImpl() }

    // 즐겨찾기 토글 Mutation - 현재 책으로 미리 생성
    val toggleFavoriteMutation = rememberMutation(
        key = context.createToggleFavoriteQueryKey(currentBook)
    )

    // DroidKaigi 패턴: EventEffect로 mutation 처리
    EventEffect(eventFlow) { event ->
        when (event) {
            is DetailUiEvent.FavoriteToggle -> {
                // EventEffect에서 mutation 실행 (suspend 함수 지원)
                toggleFavoriteMutation.mutate(Unit)
            }
            is DetailUiEvent.ShowToast -> {
                // UserMessageStateHolder로 토스트 처리
                userMessageStateHolder.showMessage(event.message)
            }
        }
    }

    val onAction: (DetailUiAction) -> Unit = remember(currentBook) {
        { action ->
            when (action) {
                is DetailUiAction.OnFavoriteClick -> {
                    // 수동 Optimistic update
                    val newFavoriteStatus = !currentBook.isFavorites
                    currentBook = currentBook.copy(isFavorites = newFavoriteStatus)

                    // EventEffect로 mutation 처리
                    eventFlow.tryEmit(DetailUiEvent.FavoriteToggle)

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
        userMessageStateHolder = userMessageStateHolder,
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
