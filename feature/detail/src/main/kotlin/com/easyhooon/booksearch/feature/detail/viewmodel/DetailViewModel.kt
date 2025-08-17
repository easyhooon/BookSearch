package com.easyhooon.booksearch.feature.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.easyhooon.booksearch.core.common.UiText
import com.easyhooon.booksearch.core.common.mapper.toModel
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.navigation.Route
import com.easyhooon.booksearch.feature.detail.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val book = savedStateHandle.toRoute<Route.Detail>(typeMap = Route.Detail.typeMap).book

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<DetailUiEvent>()
    val uiEvent: Flow<DetailUiEvent> = _uiEvent.receiveAsFlow()

    init {
        initBook(book)
    }

    private fun initBook(book: BookUiModel) {
        _uiState.update { it.copy(book = book) }
    }

    fun onAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.OnBackClick -> navigateBack()
            is DetailUiAction.OnFavoritesClick -> toggleFavorites()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(DetailUiEvent.NavigateBack)
        }
    }

    private fun toggleFavorites() {
        viewModelScope.launch {
            val currentBook = _uiState.value.book
            if (currentBook.isFavorites) {
                repository.deleteBook(currentBook.isbn)
                _uiEvent.send(DetailUiEvent.ShowToast(UiText.StringResource(R.string.delete_favorites)))
                _uiState.update { it.copy(book = currentBook.copy(isFavorites = false)) }
            } else {
                repository.insertBook(currentBook.toModel())
                _uiEvent.send(DetailUiEvent.ShowToast(UiText.StringResource(R.string.insert_favorites)))
                _uiState.update { it.copy(book = currentBook.copy(isFavorites = true)) }
            }
        }
    }
}
