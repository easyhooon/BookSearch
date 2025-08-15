package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiAction
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiEvent
import com.easyhooon.booksearch.feature.search.viewmodel.SearchUiState
import com.easyhooon.booksearch.feature.search.viewmodel.SearchViewModel

@Composable
internal fun SearchRoute(
    padding: PaddingValues,
    navigateToDetail: (String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when(event) {
            is SearchUiEvent.NavigateToDetail -> navigateToDetail(event.isbn)
        }
    }

    SearchScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun SearchScreen(
    padding: PaddingValues,
    uiState: SearchUiState,
    onAction: (SearchUiAction) -> Unit,
) {

}

