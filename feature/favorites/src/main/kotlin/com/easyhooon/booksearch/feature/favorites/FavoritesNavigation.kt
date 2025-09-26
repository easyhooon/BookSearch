package com.easyhooon.booksearch.feature.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.navigation.MainTabRoute
import dagger.hilt.android.EntryPointAccessors

fun NavController.navigateToFavorites(navOptions: NavOptions) {
    navigate(MainTabRoute.Favorites, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
) {
    composable<MainTabRoute.Favorites> {
        val context = LocalContext.current
        val favoritesScreenContext = remember {
            EntryPointAccessors.fromApplication(
                context,
                FavoritesScreenContextEntryPoint::class.java
            ).favoritesScreenContext()
        }
        
        with(favoritesScreenContext) {
            FavoritesScreenRoot(
                innerPadding = innerPadding,
                onBookClick = navigateToDetail,
            )
        }
    }
}
