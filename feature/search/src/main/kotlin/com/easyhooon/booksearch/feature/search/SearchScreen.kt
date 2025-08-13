package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
internal fun SearchRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    navigateToDetail: (String) -> Unit,
) {
    SearchScreen()
}

@Composable
internal fun SearchScreen() {}

