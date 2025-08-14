package com.easyhooon.booksearch.core.data.di

import com.easyhooon.booksearch.core.data.DefaultBookRepository
import com.easyhooon.booksearch.core.domain.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(defaultBookRepository: DefaultBookRepository): BookRepository
}
