package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.easyhooon.booksearch.core.common.ObserveAsEvents
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiAction
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiEvent
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailUiState
import com.easyhooon.booksearch.feature.detail.viewmodel.DetailViewModel

@Composable
internal fun DetailRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: DetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when(event) {
            is DetailUiEvent.NavigateBack -> popBackStack
        }
    }

    DetailScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun DetailScreen(
    padding: PaddingValues,
    uiState: DetailUiState,
    onAction: (DetailUiAction) -> Unit,
) {}
