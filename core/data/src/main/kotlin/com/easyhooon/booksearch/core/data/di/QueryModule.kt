package com.easyhooon.booksearch.core.data.di

import com.easyhooon.booksearch.core.data.query.SearchBooksQuery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object QueryModule {

    @Provides
    @Singleton
    internal fun provideSearchBooksQuery(query: SearchBooksQuery): SearchBooksQuery {
        SearchBooksQuery.instance = query
        return query
    }
}
