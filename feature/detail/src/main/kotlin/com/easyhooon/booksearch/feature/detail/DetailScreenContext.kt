package com.easyhooon.booksearch.feature.detail

import com.easyhooon.booksearch.core.domain.repository.BookRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class DetailScreenContext @Inject constructor(
    val bookRepository: BookRepository,
) {}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DetailScreenContextEntryPoint {
    fun detailScreenContext(): DetailScreenContext
}
