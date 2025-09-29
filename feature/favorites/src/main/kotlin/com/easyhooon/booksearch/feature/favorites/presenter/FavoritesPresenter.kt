package com.easyhooon.booksearch.feature.favorites.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.EventFlow
import com.easyhooon.booksearch.core.common.compose.providePresenterDefaults
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.feature.favorites.FavoritesScreenContext
import com.easyhooon.booksearch.feature.favorites.FavoritesSortType
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberMutation
import soil.query.compose.rememberSubscription

data class FavoritesUiState(
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.LATEST,
    val isPriceFilterEnabled: Boolean = false,
    val favoriteBooks: ImmutableList<BookUiModel> = persistentListOf(),
)

sealed interface FavoritesScreenEvent {
    data object Search : FavoritesScreenEvent
    data object ClearSearch : FavoritesScreenEvent
    data object ToggleFilter : FavoritesScreenEvent
    data object ToggleSort : FavoritesScreenEvent
    data class ToggleFavorite(val book: BookUiModel) : FavoritesScreenEvent
}

@OptIn(ExperimentalSoilQueryApi::class)
context(context: FavoritesScreenContext)
@Composable
fun FavoritesPresenter(
    eventFlow: EventFlow<FavoritesScreenEvent>,
    queryState: TextFieldState,
): FavoritesUiState = providePresenterDefaults {
    var searchQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(FavoritesSortType.LATEST) }
    var isPriceFilterEnabled by rememberRetained { mutableStateOf(false) }

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


    EventEffect(eventFlow) { event ->
        when (event) {
            is FavoritesScreenEvent.Search -> {
                searchQuery = queryState.text.toString()
            }
            is FavoritesScreenEvent.ClearSearch -> {
                queryState.clearText()
                searchQuery = ""
            }
            is FavoritesScreenEvent.ToggleFilter -> {
                isPriceFilterEnabled = !isPriceFilterEnabled
            }
            is FavoritesScreenEvent.ToggleSort -> {
                sortType = sortType.next()
            }
            is FavoritesScreenEvent.ToggleFavorite -> {
                toggleMutationKey = context.createToggleFavoriteQueryKey(event.book)
                toggleFavoriteMutation?.mutate(Unit)
            }
        }
    }

    FavoritesUiState(
        searchQuery = searchQuery,
        sortType = sortType,
        isPriceFilterEnabled = isPriceFilterEnabled,
        favoriteBooks = favoriteBooks,
    )
}
