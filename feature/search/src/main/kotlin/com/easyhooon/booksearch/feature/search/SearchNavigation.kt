package com.easyhooon.booksearch.feature.search

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

fun NavController.navigateToSearch(navOptions: NavOptions) {
    navigate(MainTabRoute.Search, navOptions)
}

fun NavGraphBuilder.searchGraph(
    innerPadding: PaddingValues,
    navigateToDetail: (BookUiModel) -> Unit,
) {
    composable<MainTabRoute.Search> {
        val context = LocalContext.current
        val searchScreenContext = remember {
            EntryPointAccessors.fromApplication(
                context,
                SearchScreenContextEntryPoint::class.java
            ).searchScreenContext()
        }
        
        with(searchScreenContext) {
            SearchScreenRoot(
                innerPadding = innerPadding,
                onBookClick = navigateToDetail,
            )
        }
    }
}
