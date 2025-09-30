package com.easyhooon.booksearch.feature.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.navigation.BookType
import com.easyhooon.booksearch.core.navigation.Route
import dagger.hilt.android.EntryPointAccessors
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
        val route = it.toRoute<Route.Detail>()
        val context = LocalContext.current
        val detailScreenContext = remember {
            EntryPointAccessors.fromApplication(
                context,
                DetailScreenContextEntryPoint::class.java,
            ).detailScreenContext()
        }

        with(detailScreenContext) {
            DetailScreenRoot(
                initialBook = route.book,
                innerPadding = innerPadding,
                onNavigateBack = popBackStack,
            )
        }
    }
}
