package com.easyhooon.booksearch.feature.detail.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.EventFlow
import com.easyhooon.booksearch.core.common.compose.providePresenterDefaults
import com.easyhooon.booksearch.core.common.toast.UserMessageStateHolder
import com.easyhooon.booksearch.core.common.toast.UserMessageStateHolderImpl
import com.easyhooon.booksearch.feature.detail.DetailScreenContext
import io.github.takahirom.rin.rememberRetained
import soil.query.compose.rememberMutation

data class DetailUiState(
    val book: BookUiModel,
    val userMessageStateHolder: UserMessageStateHolder,
)

sealed interface DetailScreenEvent {
    data object ToggleFavorite : DetailScreenEvent
    data class ShowToast(val message: String) : DetailScreenEvent
}

context(context: DetailScreenContext)
@Composable
fun DetailPresenter(
    initialBook: BookUiModel,
    eventFlow: EventFlow<DetailScreenEvent>,
): DetailUiState = providePresenterDefaults {
    var currentBook by rememberRetained { mutableStateOf(initialBook) }
    val userMessageStateHolder = remember { UserMessageStateHolderImpl() }

    // 즐겨찾기 토글 Mutation - 현재 책으로 미리 생성
    val toggleFavoriteMutation = rememberMutation(
        key = context.createToggleFavoriteQueryKey(currentBook)
    )

    // DroidKaigi 패턴: EventEffect로 mutation 처리
    EventEffect(eventFlow) { event ->
        when (event) {
            is DetailScreenEvent.ToggleFavorite -> {
                // Optimistic update
                val newFavoriteStatus = !currentBook.isFavorites
                currentBook = currentBook.copy(isFavorites = newFavoriteStatus)

                // EventEffect에서 mutation 실행 (suspend 함수 지원)
                toggleFavoriteMutation.mutate(Unit)

                // Toast 메시지 표시
                val message = if (newFavoriteStatus) {
                    "즐겨찾기에 추가되었습니다"
                } else {
                    "즐겨찾기에서 삭제되었습니다"
                }
                userMessageStateHolder.showMessage(message)
            }
            is DetailScreenEvent.ShowToast -> {
                // UserMessageStateHolder로 토스트 처리
                userMessageStateHolder.showMessage(event.message)
            }
        }
    }

    DetailUiState(
        book = currentBook,
        userMessageStateHolder = userMessageStateHolder,
    )
}
