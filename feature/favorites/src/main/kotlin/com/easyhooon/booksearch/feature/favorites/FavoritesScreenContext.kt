package com.easyhooon.booksearch.feature.favorites

import com.easyhooon.booksearch.core.data.query.DefaultGetFavoriteBooksQueryKey
import com.easyhooon.booksearch.core.data.query.GetFavoriteBooksQueryKey
import com.easyhooon.booksearch.core.data.query.DefaultToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class FavoritesScreenContext @Inject constructor(
    private val getFavoriteBooksQueryKey: DefaultGetFavoriteBooksQueryKey,
    private val toggleFavoriteQueryKey: DefaultToggleFavoriteQueryKey,
) {
    fun createGetFavoriteBooksQueryKey(
        query: String = "",
        sortType: String = "LATEST",
        isPriceFilterEnabled: Boolean = false,
    ): GetFavoriteBooksQueryKey = getFavoriteBooksQueryKey.create(query, sortType, isPriceFilterEnabled)
        
    fun createToggleFavoriteQueryKey(book: com.easyhooon.booksearch.core.common.model.BookUiModel): ToggleFavoriteQueryKey =
        toggleFavoriteQueryKey.create(book)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoritesScreenContextEntryPoint {
    fun favoritesScreenContext(): FavoritesScreenContext
}