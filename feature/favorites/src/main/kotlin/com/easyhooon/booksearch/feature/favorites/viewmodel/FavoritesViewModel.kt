package com.easyhooon.booksearch.feature.favorites.viewmodel

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyhooon.booksearch.core.common.UiText
import com.easyhooon.booksearch.core.common.mapper.toModel
import com.easyhooon.booksearch.core.common.mapper.toUiModel
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.usecase.ToggleFavoriteUseCase
import com.easyhooon.booksearch.feature.favorites.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: BookRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<FavoritesUiEvent>()
    val uiEvent: Flow<FavoritesUiEvent> = _uiEvent.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteBooks: StateFlow<ImmutableList<BookUiModel>> = _uiState
        .flatMapLatest { state ->
            val query = state.searchQuery
            val sortType = state.sortType

            val booksFlow = if (query.isBlank()) {
                repository.favoriteBooks
            } else {
                repository.searchFavoritesByTitle(query)
            }

            booksFlow.map { books ->
                val uiBooks = books.map { it.toUiModel().copy(isFavorites = true) }

                when (sortType) {
                    FavoritesSortType.TITLE_ASC -> uiBooks.sortedBy { it.title }
                    FavoritesSortType.TITLE_DESC -> uiBooks.sortedByDescending { it.title }
                }.toImmutableList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = persistentListOf(),
        )

    fun onAction(action: FavoritesUiAction) {
        when (action) {
            is FavoritesUiAction.OnBookClick -> navigateToBookDetail(action.book)
            is FavoritesUiAction.OnSearchClick -> updateSearchQuery()
            is FavoritesUiAction.OnClearClick -> clearQuery()
            is FavoritesUiAction.OnSortClick -> toggleSortType()
            is FavoritesUiAction.OnFilterClick -> togglePriceFilter()
            is FavoritesUiAction.OnFavoritesClick -> toggleFavorites(action.book)
        }
    }

    private fun navigateToBookDetail(book: BookUiModel) {
        viewModelScope.launch {
            _uiEvent.send(FavoritesUiEvent.NavigateToDetail(book))
        }
    }

    private fun updateSearchQuery() {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = currentState.queryState.text.toString())
        }
    }

    private fun clearQuery() {
        _uiState.value.queryState.clearText()

        _uiState.update { currentState ->
            currentState.copy(searchQuery = "")
        }
    }

    private fun toggleSortType() {
        _uiState.update { currentState ->
            currentState.copy(sortType = currentState.sortType.toggle())
        }
    }

    private fun togglePriceFilter() {
        _uiState.update { currentState ->
            currentState.copy(isPriceFilterEnabled = !currentState.isPriceFilterEnabled)
        }
    }

    private fun toggleFavorites(book: BookUiModel) {
        viewModelScope.launch {
            toggleFavoriteUseCase(book.toModel())
            _uiEvent.send(FavoritesUiEvent.ShowToast(UiText.StringResource(R.string.delete_favorites)))
        }
    }
}
