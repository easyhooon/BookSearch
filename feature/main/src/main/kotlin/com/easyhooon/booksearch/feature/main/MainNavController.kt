package com.easyhooon.booksearch.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.navigation.MainTabRoute
import com.easyhooon.booksearch.core.navigation.Route
import com.easyhooon.booksearch.feature.detail.navigateToDetail
import com.easyhooon.booksearch.feature.favorites.navigateToFavorites
import com.easyhooon.booksearch.feature.main.component.MainTab
import com.easyhooon.booksearch.feature.search.navigateToSearch

internal class MainNavController(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = MainTab.SEARCH.route

    val currentTab: MainTab?
        @Composable get() = MainTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    fun navigate(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainTab.SEARCH -> navController.navigateToSearch(navOptions)
            MainTab.FAVORITES -> navController.navigateToFavorites(navOptions)
        }
    }

    fun navigateToDetail(book: BookUiModel) {
        navController.navigateToDetail(book)
    }

    // https://github.com/droidknights/DroidKnights2023_App/pull/243/commits/4bfb6d13908eaaab38ab3a59747d628efa3893cb
    fun popBackStackIfNotSearch() {
        if (!isSameCurrentDestination<MainTabRoute.Search>()) {
            navController.popBackStack()
        }
    }

    private inline fun <reified T : Route> isSameCurrentDestination(): Boolean {
        return navController.currentDestination?.hasRoute<T>() == true
    }

    @Composable
    fun shouldShowBottomBar() = MainTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}

@Composable
internal fun rememberMainNavController(
    navController: NavHostController = rememberNavController(),
): MainNavController = remember(navController) {
    MainNavController(navController)
}
