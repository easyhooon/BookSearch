package com.easyhooon.booksearch.feature.favorites.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.feature.favorites.FavoritesScreenContext
import com.easyhooon.booksearch.feature.favorites.FavoritesSortType
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberMutation
import soil.query.compose.rememberSubscription

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
    data class OnFavoriteToggle(val book: BookUiModel) : FavoritesUiAction
}


data class FavoritesPresenterState(
    val uiState: FavoritesUiState,
    val onAction: (FavoritesUiAction) -> Unit,
)

@OptIn(ExperimentalSoilQueryApi::class)
context(context: FavoritesScreenContext)
@Composable
fun FavoritesPresenter(
    queryState: TextFieldState,
): FavoritesPresenterState = providePresenterDefaults {
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.LATEST) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    // Room에서 즐겨찾기 데이터 구독
    val favoritesSubscription = rememberSubscription(
        key = context.createFavoriteBooksSubscriptionKey(
            query = searchQuery,
            sortType = sortType.value,
            isPriceFilterEnabled = isPriceFilterEnabled,
        )
    )

    val favoriteBooks = favoritesSubscription.data?.toImmutableList() ?: persistentListOf()

    // DroidKaigi 패턴: Mutation을 Presenter에서 생성 (더미 값으로 초기화)
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation<Boolean, Unit>(key = key)
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
                is FavoritesUiAction.OnFavoriteToggle -> {
                    // DroidKaigi 패턴: Mutation 사용해서 즐겨찾기 토글
                    coroutineScope.launch {
                        toggleMutationKey = context.createToggleFavoriteQueryKey(action.book)
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

@Composable
private inline fun providePresenterDefaults(
    content: @Composable () -> FavoritesPresenterState
): FavoritesPresenterState = content()
