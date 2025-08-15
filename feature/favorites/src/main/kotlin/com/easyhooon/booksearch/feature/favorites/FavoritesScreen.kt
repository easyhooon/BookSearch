package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiAction
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiEvent
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesUiState
import com.easyhooon.booksearch.feature.favorites.viewmodel.FavoritesViewModel

@Composable
internal fun FavoritesRoute(
    padding: PaddingValues,
    navigateToDetail: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when(event) {
            is FavoritesUiEvent.NavigateToDetail -> navigateToDetail(event.isbn)
        }
    }

    FavoritesScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun FavoritesScreen(
    padding: PaddingValues,
    uiState: FavoritesUiState,
    onAction: (FavoritesUiAction) -> Unit,
) {}
