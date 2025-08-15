package com.easyhooon.booksearch.feature.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
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
class DetailViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<DetailUiEvent>()
    val uiEvent: Flow<DetailUiEvent> = _uiEvent.receiveAsFlow()

    fun onAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.OnNackClick -> navigateBack()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(DetailUiEvent.NavigateBack)
        }
    }
}
