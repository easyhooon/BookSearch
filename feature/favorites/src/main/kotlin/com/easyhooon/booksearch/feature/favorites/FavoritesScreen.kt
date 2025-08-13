package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.navigation.MainTabRoute

@Composable
internal fun FavoritesRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    navigateToDetail: (String) -> Unit,
) {
    FavoritesScreen()
}

@Composable
internal fun FavoritesScreen() {}