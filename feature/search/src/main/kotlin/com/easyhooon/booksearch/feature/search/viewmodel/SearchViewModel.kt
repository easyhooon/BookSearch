package com.easyhooon.booksearch.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyhooon.booksearch.core.domain.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SearchUiEvent>()
    val uiEvent: Flow<SearchUiEvent> = _uiEvent.receiveAsFlow()

    fun onAction(action: SearchUiAction) {
        when (action) {
            is SearchUiAction.OnBookCardClick -> navigateToBookDetail(action.isbn)
        }
    }

    private fun navigateToBookDetail(isbn: String) {
        viewModelScope.launch {
            _uiEvent.send(SearchUiEvent.NavigateToDetail(isbn))
        }
    }
}
