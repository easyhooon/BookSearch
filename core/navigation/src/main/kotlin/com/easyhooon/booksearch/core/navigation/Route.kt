package com.easyhooon.booksearch.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data class BookDetail(val bookId: String) : Route
}

sealed interface MainTabRoute : Route {
    @Serializable
    data object Search : MainTabRoute

    @Serializable
    data object Favorites : MainTabRoute
}