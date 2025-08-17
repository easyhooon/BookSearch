package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.navigation.MainTabRoute

fun NavController.navigateToFavorites(navOptions: NavOptions) {
    navigate(MainTabRoute.Favorites, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
) {
    composable<MainTabRoute.Favorites> {
        FavoritesRoute(
            innerPadding = innerPadding,
            navigateToDetail = navigateToDetail,
        )
    }
}
