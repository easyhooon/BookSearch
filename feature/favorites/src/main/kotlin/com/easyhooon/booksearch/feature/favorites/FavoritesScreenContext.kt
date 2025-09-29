package com.easyhooon.booksearch.feature.favorites

import com.easyhooon.booksearch.core.data.query.DefaultToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.subscription.DefaultFavoriteBooksSubscriptionKey
import com.easyhooon.booksearch.core.data.subscription.FavoriteBooksSubscriptionKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class FavoritesScreenContext @Inject constructor(
    private val toggleFavoriteQueryKey: DefaultToggleFavoriteQueryKey,
    private val favoriteBooksSubscriptionKey: DefaultFavoriteBooksSubscriptionKey,
) {

    fun createToggleFavoriteQueryKey(book: com.easyhooon.booksearch.core.common.model.BookUiModel): ToggleFavoriteQueryKey =
        toggleFavoriteQueryKey.create(book)

    fun createFavoriteBooksSubscriptionKey(
        query: String = "",
        sortType: String = "LATEST",
        isPriceFilterEnabled: Boolean = false,
    ): FavoriteBooksSubscriptionKey = favoriteBooksSubscriptionKey.create(query, sortType, isPriceFilterEnabled)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoritesScreenContextEntryPoint {
    fun favoritesScreenContext(): FavoritesScreenContext
}
