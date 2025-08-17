package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.navigation.BookType
import com.easyhooon.booksearch.core.navigation.Route
import kotlin.reflect.typeOf

fun NavController.navigateToDetail(book: Book) {
    navigate(Route.Detail(book))
}

fun NavGraphBuilder.detailGraph(
    innerPadding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.Detail>(
        typeMap = mapOf(typeOf<Book>() to BookType),
    ) { navBackStackEntry ->
        val book = navBackStackEntry.toRoute<Route.Detail>().book
        DetailRoute(
            innerPadding = innerPadding,
            popBackStack = popBackStack,
            book = book,
        )
    }
}
