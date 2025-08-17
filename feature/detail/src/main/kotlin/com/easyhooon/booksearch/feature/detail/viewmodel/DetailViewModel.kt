package com.easyhooon.booksearch.feature.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.model.Book
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<DetailUiEvent>()
    val uiEvent: Flow<DetailUiEvent> = _uiEvent.receiveAsFlow()

    fun onAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.OnBackClick -> navigateBack()
            is DetailUiAction.OnFavoritesClick -> toggleFavorites(action.book)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(DetailUiEvent.NavigateBack)
        }
    }

    private fun toggleFavorites(book: Book) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isFavorite) {
                repository.deleteBook(book.isbn)
            } else {
                repository.insertBook(book)
            }

            _uiState.update { it.copy(isFavorite = !currentState.isFavorite) }
        }
    }
}
