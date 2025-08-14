package com.easyhooon.booksearch.core.database.di

import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.database.FavoritesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun provideFavoritesDao(database: FavoritesDatabase): FavoritesDao = database.favoritesDao()
}
