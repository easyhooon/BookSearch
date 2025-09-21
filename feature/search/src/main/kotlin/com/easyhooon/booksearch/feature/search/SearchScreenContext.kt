package com.easyhooon.booksearch.feature.search

import com.easyhooon.booksearch.core.data.query.DefaultSearchBooksQueryKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class SearchScreenContext @Inject constructor(
    val searchBooksQueryKey: DefaultSearchBooksQueryKey,
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SearchScreenContextEntryPoint {
    fun searchScreenContext(): SearchScreenContext
}