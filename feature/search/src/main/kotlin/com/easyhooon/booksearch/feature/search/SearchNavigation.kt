package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.easyhooon.booksearch.core.navigation.MainTabRoute

fun NavController.navigateToSearch(navOptions: NavOptions) {
    navigate(MainTabRoute.Search, navOptions)
}

fun NavGraphBuilder.searchGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    navigateToDetail: (String) -> Unit,
) {
    composable<MainTabRoute.Search> {
        SearchRoute(
            padding = padding,
            popBackStack = popBackStack,
            navigateToDetail = navigateToDetail
        )
    }
}