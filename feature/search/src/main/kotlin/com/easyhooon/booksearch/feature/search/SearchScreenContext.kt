package com.easyhooon.booksearch.feature.search

import com.easyhooon.booksearch.core.data.query.DefaultSearchBooksQueryKey
import com.easyhooon.booksearch.core.domain.repository.BookRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class SearchScreenContext @Inject constructor(
    val searchBooksQueryKey: DefaultSearchBooksQueryKey,
    val bookRepository: BookRepository,
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SearchScreenContextEntryPoint {
    fun searchScreenContext(): SearchScreenContext
}
