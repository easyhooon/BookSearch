package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.easyhooon.booksearch.core.navigation.Route

fun NavController.navigateToDetail(
    isbn: String,
) {
    navigate(Route.Detail(isbn))
}

fun NavGraphBuilder.detailGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.Detail> {
        DetailRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}