package com.easyhooon.booksearch.feature.detail

import com.easyhooon.booksearch.core.data.query.DefaultToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.subscription.DefaultFavoriteBookIdsSubscriptionKey
import com.easyhooon.booksearch.core.data.subscription.FavoriteBookIdsSubscriptionKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class DetailScreenContext @Inject constructor(
    private val toggleFavoriteQueryKey: DefaultToggleFavoriteQueryKey,
    private val favoriteBookIdsSubscriptionKey: DefaultFavoriteBookIdsSubscriptionKey,
) {
    fun createToggleFavoriteQueryKey(book: com.easyhooon.booksearch.core.common.model.BookUiModel): ToggleFavoriteQueryKey =
        toggleFavoriteQueryKey.create(book)
        
    fun createFavoriteBookIdsSubscriptionKey(): FavoriteBookIdsSubscriptionKey = 
        favoriteBookIdsSubscriptionKey.create()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DetailScreenContextEntryPoint {
    fun detailScreenContext(): DetailScreenContext
}
