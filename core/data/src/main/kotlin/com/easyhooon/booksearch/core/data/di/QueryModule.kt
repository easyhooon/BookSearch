package com.easyhooon.booksearch.core.data.di

import com.easyhooon.booksearch.core.data.query.DefaultSearchBooksQueryKey
import com.easyhooon.booksearch.core.data.query.GetFavoriteBooksQuery
import com.easyhooon.booksearch.core.data.query.ToggleFavoriteQuery
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
    internal fun provideToggleFavoriteQuery(query: ToggleFavoriteQuery): ToggleFavoriteQuery {
        ToggleFavoriteQuery.instance = query
        return query
    }
    
    @Provides
    @Singleton
    internal fun provideGetFavoriteBooksQuery(query: GetFavoriteBooksQuery): GetFavoriteBooksQuery {
        GetFavoriteBooksQuery.instance = query
        return query
    }
    
}
