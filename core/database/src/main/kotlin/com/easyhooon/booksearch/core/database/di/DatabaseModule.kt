package com.easyhooon.booksearch.core.database.di

import android.content.Context
import androidx.room.Room
import com.easyhooon.booksearch.core.database.FavoritesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideFavoritesDatabase(@ApplicationContext context: Context): FavoritesDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            FavoritesDatabase::class.java,
            FavoritesDatabase.DATABASE_NAME
        ).build()
}