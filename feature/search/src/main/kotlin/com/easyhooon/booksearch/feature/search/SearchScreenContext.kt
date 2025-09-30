package com.easyhooon.booksearch.feature.search

import com.easyhooon.booksearch.core.data.query.DefaultSearchBooksQueryKey
import com.easyhooon.booksearch.core.data.query.DefaultToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.subscription.DefaultFavoriteBookIdsSubscriptionKey
import com.easyhooon.booksearch.core.data.subscription.FavoriteBookIdsSubscriptionKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class SearchScreenContext @Inject constructor(
    val searchBooksQueryKey: DefaultSearchBooksQueryKey,
    private val favoriteBookIdsSubscriptionKey: DefaultFavoriteBookIdsSubscriptionKey,
    private val toggleFavoriteQueryKey: DefaultToggleFavoriteQueryKey,
) {
    fun createFavoriteBookIdsSubscriptionKey(): FavoriteBookIdsSubscriptionKey = 
        favoriteBookIdsSubscriptionKey.create()
        
    fun createToggleFavoriteQueryKey(book: com.easyhooon.booksearch.core.common.model.BookUiModel): ToggleFavoriteQueryKey =
        toggleFavoriteQueryKey.create(book)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SearchScreenContextEntryPoint {
    fun searchScreenContext(): SearchScreenContext
}