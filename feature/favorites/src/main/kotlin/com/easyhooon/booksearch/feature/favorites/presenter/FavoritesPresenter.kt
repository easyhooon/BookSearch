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
import soil.query.compose.rememberMutation

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
    var toggleMutationKey by remember { mutableStateOf<ToggleFavoriteQueryKey?>(null) }
    val toggleFavoriteMutation = toggleMutationKey?.let { key ->
        rememberMutation(key = key)
    }

    EventEffect(eventFlow) { event ->
        when (event) {
            is FavoritesScreenEvent.Search -> {
                onSearchQuery(queryState.text.toString())
            }
            is FavoritesScreenEvent.ClearSearch -> {
                queryState.clearText()
                onSearchQuery("")
            }
            is FavoritesScreenEvent.ToggleFilter -> {
                onPriceFilter(!isPriceFilterEnabled)
            }
            is FavoritesScreenEvent.ToggleSort -> {
                onSortType(sortType.next())
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
