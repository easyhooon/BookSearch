package com.easyhooon.booksearch.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.easyhooon.booksearch.core.common.compose.SafeLaunchedEffect
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.feature.search.presenter.SearchPresenter
import com.orhanobut.logger.Logger
import io.github.takahirom.rin.rememberRetained
import soil.query.annotation.ExperimentalSoilQueryApi
import soil.query.compose.rememberSubscription

@OptIn(ExperimentalSoilQueryApi::class)
context(context: SearchScreenContext)
@Composable
fun SearchScreenRoot(
    innerPadding: PaddingValues,
    onBookClick: (BookUiModel) -> Unit,
) {
    val queryState = rememberRetained { TextFieldState() }

    // DroidKaigi 방식: subscription으로 즐겨찾기 ID들을 구독
    val favoriteIdsSubscription = rememberSubscription(
        key = context.createFavoriteBookIdsSubscriptionKey()
    )

    val favoriteBookIds = favoriteIdsSubscription.data ?: emptySet()
    
    SafeLaunchedEffect(favoriteBookIds) {
        Logger.d("SearchScreenRoot favoriteBookIds changed: $favoriteBookIds")
    }

    val presenterState = SearchPresenter(
        queryState = queryState,
        favoriteBookIds = favoriteBookIds,
    )

    SearchScreen(
        innerPadding = innerPadding,
        queryState = queryState,
        uiState = presenterState.uiState,
        onAction = presenterState.onAction,
        onBookClick = onBookClick,
    )
}
