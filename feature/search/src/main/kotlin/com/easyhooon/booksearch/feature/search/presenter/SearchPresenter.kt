package com.easyhooon.booksearch.feature.search.presenter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.SearchBooksQueryKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import soil.query.compose.QueryObject
import soil.query.compose.rememberQuery

data class SearchUiState(
    val searchState: SearchState = SearchState.Idle,
    val queryState: TextFieldState = TextFieldState(),
    val sortType: SortType = SortType.ACCURACY,
    val books: ImmutableList<BookUiModel> = persistentListOf(),
    val isLastPage: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data object Success : SearchState
    data class Error(val message: String) : SearchState
}

enum class SortType(val value: String, val displayName: String) {
    ACCURACY("accuracy", "정확도순"),
    LATEST("latest", "최신순");

    fun toggle(): SortType = when (this) {
        ACCURACY -> LATEST
        LATEST -> ACCURACY
    }
}

sealed interface SearchUiAction {
    data class OnSearchClick(val query: String) : SearchUiAction
    data object OnClearClick : SearchUiAction
    data object OnLoadMore : SearchUiAction
    data object OnRetryClick : SearchUiAction
    data object OnSortClick : SearchUiAction
    data class OnBookClick(val book: BookUiModel) : SearchUiAction
}

sealed interface SearchUiEvent {
    data class NavigateToDetail(val book: BookUiModel) : SearchUiEvent
}

data class SearchPresenterState(
    val uiState: SearchUiState,
    val onAction: (SearchUiAction) -> Unit,
)

@Composable
fun SearchPresenter(
    queryState: TextFieldState = rememberRetained { TextFieldState() },
    onNavigateToDetail: (BookUiModel) -> Unit,
): SearchPresenterState {
    var currentQuery by rememberRetained { mutableStateOf("") }
    var sortType by rememberRetained { mutableStateOf(SortType.ACCURACY) }

    val uiEventChannel = remember { Channel<SearchUiEvent>() }
    val uiEvent = remember(uiEventChannel) { uiEventChannel.receiveAsFlow() }

    LaunchedEffect(uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                is SearchUiEvent.NavigateToDetail -> {
                    onNavigateToDetail(event.book)
                }
            }
        }
    }

    var currentPage by rememberRetained { mutableStateOf(1) }
    var allBooks by rememberRetained { mutableStateOf<List<BookUiModel>>(emptyList()) }

    val query: QueryObject<List<BookUiModel>> = rememberQuery(
        key = SearchBooksQueryKey(
            query = currentQuery,
            sort = sortType.value,
            page = currentPage,
            size = 20
        )
    )

    // 새로운 검색 결과 처리
    LaunchedEffect(query.data) {
        query.data?.let { newBooks ->
            allBooks = if (currentPage == 1) {
                newBooks
            } else {
                allBooks + newBooks
            }
        }
    }

    val books by remember {
        derivedStateOf {
            allBooks.toImmutableList()
        }
    }

    val searchState by remember {
        derivedStateOf {
            when {
                query.isPending && currentQuery.isNotEmpty() -> SearchState.Loading
                query.isFailure -> SearchState.Error(query.error?.message ?: "Unknown error")
                query.isSuccess && currentQuery.isNotEmpty() -> SearchState.Success
                else -> SearchState.Idle
            }
        }
    }

    // Action 핸들링도 presenter 내부에서 처리
    val onAction: (SearchUiAction) -> Unit = remember {
        { action ->
            when (action) {
                is SearchUiAction.OnSearchClick -> {
                    currentQuery = action.query
                    currentPage = 1
                    allBooks = emptyList()
                }
                is SearchUiAction.OnClearClick -> {
                    queryState.clearText()
                    currentQuery = ""
                    currentPage = 1
                    allBooks = emptyList()
                }
                is SearchUiAction.OnLoadMore -> {
                    currentPage = currentPage + 1
                }
                is SearchUiAction.OnRetryClick -> {
                    currentPage = 1
                    allBooks = emptyList()
                }
                is SearchUiAction.OnSortClick -> {
                    sortType = sortType.toggle()
                    currentPage = 1
                    allBooks = emptyList()
                }
                is SearchUiAction.OnBookClick -> {
                    uiEventChannel.trySend(SearchUiEvent.NavigateToDetail(action.book))
                }
            }
        }
    }

    val uiState = SearchUiState(
        searchState = searchState,
        queryState = queryState,
        sortType = sortType,
        books = books,
        isLastPage = (query.data?.size ?: 0) < 20, // 20개 미만이면 마지막 페이지
        hasError = query.isFailure,
        errorMessage = query.error?.message,
    )

    return SearchPresenterState(
        uiState = uiState,
        onAction = onAction,
    )
}
