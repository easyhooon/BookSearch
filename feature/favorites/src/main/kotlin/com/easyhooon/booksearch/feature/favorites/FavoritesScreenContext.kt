package com.easyhooon.booksearch.feature.favorites

import com.easyhooon.booksearch.core.domain.repository.BookRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class FavoritesScreenContext @Inject constructor(
    val bookRepository: BookRepository,
) {}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoritesScreenContextEntryPoint {
    fun favoritesScreenContext(): FavoritesScreenContext
}
