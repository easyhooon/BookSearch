package com.easyhooon.booksearch.feature.search.viewmodel

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyhooon.booksearch.core.common.mapper.toUiModel
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.ui.component.FooterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BookRepository,
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 10
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SearchUiEvent>()
    val uiEvent: Flow<SearchUiEvent> = _uiEvent.receiveAsFlow()

    val favoriteBooks: StateFlow<List<Book>> = repository.favoriteBooks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf(),
        )

    fun onAction(action: SearchUiAction) {
        when (action) {
            is SearchUiAction.OnBookClick -> navigateToBookDetail(action.book)
            is SearchUiAction.OnSearchClick -> searchBook(action.query)
            is SearchUiAction.OnClearClick -> clearQuery()
            is SearchUiAction.OnLoadMore -> loadMore()
            is SearchUiAction.OnRetryClick -> retry()
            is SearchUiAction.OnSortClick -> toggleSortType()
        }
    }

    private fun navigateToBookDetail(book: BookUiModel) {
        viewModelScope.launch {
            _uiEvent.send(SearchUiEvent.NavigateToDetail(book))
        }
    }

    private fun searchBook(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            val currentState = _uiState.value
            val isFirstPage = query != currentState.currentQuery

            if (isFirstPage) {
                _uiState.update { state ->
                    state.copy(
                        searchState = SearchState.Loading,
                        currentQuery = query,
                        currentPage = 1,
                        books = persistentListOf(),
                        isLastPage = false,
                    )
                }
            } else {
                _uiState.update { it.copy(footerState = FooterState.Loading) }
            }
            val page = if (isFirstPage) 1 else currentState.currentPage

            repository.searchBook(
                query = query,
                sort = currentState.sortType.value,
                page = page,
                size = PAGE_SIZE,
            ).onSuccess { searchResult ->
                val searchBooks = searchResult.documents.map { book ->
                    val isFavorite = favoriteBooks.value.any { it.isbn == book.isbn }
                    book.toUiModel().copy(isFavorites = isFavorite)
                }

                val newBooks = if (isFirstPage) {
                    searchBooks
                } else {
                    currentState.books + searchBooks
                }

                _uiState.update { state ->
                    state.copy(
                        searchState = SearchState.Success,
                        footerState = FooterState.Idle,
                        books = newBooks.toImmutableList(),
                        currentPage = page + 1,
                        isLastPage = searchResult.meta.isEnd,
                    )
                }
            }.onFailure { exception ->
                if (isFirstPage) {
                    _uiState.update { state ->
                        state.copy(
                            searchState = SearchState.Error(exception),
                            footerState = FooterState.Idle,
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            footerState = FooterState.Error(
                                exception.message ?: "Unknown error occurred",
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun clearQuery() {
        _uiState.value.queryState.clearText()
    }

    private fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLastPage || currentState.footerState is FooterState.Loading) {
            return
        }

        searchBook(currentState.currentQuery)
    }

    private fun retry() {
        val query = _uiState.value.currentQuery
        if (query.isNotEmpty()) {
            searchBook(query)
        }
    }

    private fun toggleSortType() {
        val currentState = _uiState.value
        val newSortType = currentState.sortType.toggle()

        _uiState.update { state ->
            state.copy(
                sortType = newSortType,
                books = persistentListOf(),
                currentPage = 1,
                isLastPage = false,
                searchState = SearchState.Idle,
                footerState = FooterState.Idle,
            )
        }

        // 현재 검색어가 있으면 새로운 정렬로 다시 검색
        if (currentState.currentQuery.isNotBlank()) {
            searchBook(currentState.currentQuery)
        }
    }
}
