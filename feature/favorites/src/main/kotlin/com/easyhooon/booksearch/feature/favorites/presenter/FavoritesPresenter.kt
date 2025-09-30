package com.easyhooon.booksearch.feature.favorites.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.compose.EventEffect
import com.easyhooon.booksearch.core.common.compose.EventFlow
import com.easyhooon.booksearch.core.common.compose.providePresenterDefaults
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.feature.favorites.FavoritesScreenContext
import com.easyhooon.booksearch.feature.favorites.FavoritesSortType
import com.orhanobut.logger.Logger
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import soil.query.compose.rememberMutation

data class FavoritesUiState(
    val searchQuery: String = "",
    val sortType: FavoritesSortType = FavoritesSortType.TITLE_ASC,
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

context(context: FavoritesScreenContext)
@Composable
fun FavoritesPresenter(
    eventFlow: EventFlow<FavoritesScreenEvent>,
    queryState: TextFieldState,
    searchQuery: String,
    sortType: FavoritesSortType,
    isPriceFilterEnabled: Boolean,
    favoriteBooks: ImmutableList<BookUiModel>,
    onSearchQuery: (String) -> Unit,
    onSortType: (FavoritesSortType) -> Unit,
    onPriceFilter: (Boolean) -> Unit,
): FavoritesUiState = providePresenterDefaults {
    var toggleMutationKey by rememberRetained { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }

    EventEffect(eventFlow) { event ->
        Logger.d("FavoritesPresenter: Received event: $event")
        when (event) {
            is FavoritesScreenEvent.Search -> {
                Logger.d("FavoritesPresenter: Processing Search event")
                onSearchQuery(queryState.text.toString())
            }
            is FavoritesScreenEvent.ClearSearch -> {
                Logger.d("FavoritesPresenter: Processing ClearSearch event")
                queryState.clearText()
                onSearchQuery("")
            }
            is FavoritesScreenEvent.ToggleFilter -> {
                Logger.d("FavoritesPresenter: Processing ToggleFilter event - current: $isPriceFilterEnabled")
                onPriceFilter(!isPriceFilterEnabled)
            }
            is FavoritesScreenEvent.ToggleSort -> {
                Logger.d("FavoritesPresenter: Processing ToggleSort event - current: $sortType")
                onSortType(sortType.next())
            }
            is FavoritesScreenEvent.ToggleFavorite -> {
                Logger.d("FavoritesPresenter: Processing ToggleFavorite event for book: ${event.book.title}")
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
