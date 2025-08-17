package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.navigation.BookType
import com.easyhooon.booksearch.core.navigation.Route
import kotlin.reflect.typeOf

fun NavController.navigateToDetail(book: BookUiModel) {
    navigate(Route.Detail(book))
}

fun NavGraphBuilder.detailGraph(
    innerPadding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.Detail>(
        typeMap = mapOf(typeOf<BookUiModel>() to BookType),
    ) {
        DetailRoute(
            innerPadding = innerPadding,
            popBackStack = popBackStack,
        )
    }
}
