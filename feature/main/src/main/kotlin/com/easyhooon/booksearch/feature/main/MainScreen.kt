package com.easyhooon.booksearch.feature.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.easyhooon.booksearch.feature.detail.detailGraph
import com.easyhooon.booksearch.feature.favorites.favoritesGraph
import com.easyhooon.booksearch.feature.main.component.BookSearchScaffold
import com.easyhooon.booksearch.feature.main.component.MainBottomBar
import com.easyhooon.booksearch.feature.main.component.MainTab
import com.easyhooon.booksearch.feature.search.searchGraph
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MainScreen(
    navigator: MainNavController = rememberMainNavController()
) {
    BookSearchScaffold(
        bottomBar = {
            MainBottomBar(
                visible = navigator.shouldShowBottomBar(),
                tabs = MainTab.entries.toImmutableList(),
                currentTab = navigator.currentTab,
                onTabSelected = {
                    navigator.navigate(it)
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            searchGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStackIfNotSearch,
                navigateToDetail = {}
            )

            favoritesGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStackIfNotSearch,
                navigateToDetail = {}
            )

            detailGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStackIfNotSearch
            )
        }
    }
}
