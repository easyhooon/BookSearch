package com.easyhooon.booksearch.feature.favorites.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.rememberEventFlow
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.GetFavoriteBooksQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.feature.favorites.FavoritesSortType
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import soil.query.compose.rememberMutation
import soil.query.compose.rememberQuery

data class FavoritesUiState(
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.LATEST,
    val isPriceFilterEnabled: Boolean = false,
    val favoriteBooks: ImmutableList<BookUiModel> = persistentListOf(),
)

sealed interface FavoritesUiAction {
    data object OnSearchClick : FavoritesUiAction
    data object OnClearClick : FavoritesUiAction
    data object OnFilterClick : FavoritesUiAction
    data object OnSortClick : FavoritesUiAction
    data class OnBookClick(val book: BookUiModel) : FavoritesUiAction
    data class OnFavoriteToggle(val book: BookUiModel) : FavoritesUiAction
}

sealed interface FavoritesUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : FavoritesUiEvent
}

data class FavoritesPresenterState(
    val uiState: FavoritesUiState,
    val onAction: (FavoritesUiAction) -> Unit,
)

@Composable
fun FavoritesPresenter(
    queryState: TextFieldState = rememberRetained { TextFieldState() },
    onNavigateToDetail: (BookUiModel) -> Unit,
): FavoritesPresenterState {
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.LATEST) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val eventFlow = rememberEventFlow<FavoritesUiEvent>()

    // Room에서 즐겨찾기 데이터 가져오기
    val favoritesQuery = rememberQuery(
        key = GetFavoriteBooksQueryKey(
            query = searchQuery,
            sortType = sortType.value,
            isPriceFilterEnabled = isPriceFilterEnabled,
        )
    )
    
    val favoriteBooks = favoritesQuery.data?.toImmutableList() ?: persistentListOf()
    
    // DroidKaigi 패턴: Mutation을 Presenter에서 생성 (더미 값으로 초기화)
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation<Boolean, Unit>(key = key)
    }

    EventEffect(eventFlow) { event ->
        when (event) {
            is FavoritesUiEvent.NavigateToDetail -> {
                onNavigateToDetail(event.book)
            }
        }
    }

    val onAction: (FavoritesUiAction) -> Unit = remember(favoriteBooks) {
        { action ->
            when (action) {
                is FavoritesUiAction.OnSearchClick -> {
                    searchQuery = queryState.text.toString()
                }
                is FavoritesUiAction.OnClearClick -> {
                    queryState.clearText()
                    searchQuery = ""
                }
                is FavoritesUiAction.OnFilterClick -> {
                    isPriceFilterEnabled = !isPriceFilterEnabled
                }
                is FavoritesUiAction.OnSortClick -> {
                    sortType = sortType.next()
                }
                is FavoritesUiAction.OnBookClick -> {
                    eventFlow.tryEmit(FavoritesUiEvent.NavigateToDetail(action.book))
                }
                is FavoritesUiAction.OnFavoriteToggle -> {
                    // DroidKaigi 패턴: Mutation 사용해서 즐겨찾기 토글
                    coroutineScope.launch {
                        toggleMutationKey = ToggleFavoriteQueryKey(action.book)
                        toggleFavoriteMutation?.mutateAsync(Unit)
                    }
                }
            }
        }
    }

    val uiState = FavoritesUiState(
        searchQuery = searchQuery,
        sortType = sortType,
        isPriceFilterEnabled = isPriceFilterEnabled,
        favoriteBooks = favoriteBooks,
    )

    return FavoritesPresenterState(
        uiState = uiState,
        onAction = onAction,
    )
}
