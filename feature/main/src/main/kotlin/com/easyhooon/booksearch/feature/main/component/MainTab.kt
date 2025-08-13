package com.easyhooon.booksearch.feature.main.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.navigation.MainTabRoute
import com.easyhooon.booksearch.core.navigation.Route
import com.easyhooon.booksearch.feature.main.R

internal enum class MainTab(
    @DrawableRes val iconResId: Int,
    @DrawableRes val selectedResId: Int,
    @StringRes val labelResId: Int,
    val contentDescription: String,
    val route: MainTabRoute,
) {
    SEARCH(
        iconResId = R.drawable.ic_search,
        selectedResId = R.drawable.ic_selected_search,
        labelResId = R.string.search_label,
        contentDescription = "Search Icon",
        route = MainTabRoute.Search,
    ),
    FAVORITES(
        iconResId = R.drawable.ic_favorites,
        selectedResId = R.drawable.ic_selected_favorites,
        labelResId = R.string.favorites_label,
        contentDescription = "Favorites Icon",
        route = MainTabRoute.Favorites,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (MainTabRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}