package com.easyhooon.booksearch.feature.detail

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.data.query.DefaultToggleFavoriteQueryKey
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQueryKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class DetailScreenContext @Inject constructor(
    private val toggleFavoriteQueryKey: DefaultToggleFavoriteQueryKey,
) {
    fun createToggleFavoriteQueryKey(book: BookUiModel): ToggleFavoriteQueryKey =
        toggleFavoriteQueryKey.create(book)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DetailScreenContextEntryPoint {
    fun detailScreenContext(): DetailScreenContext
}
