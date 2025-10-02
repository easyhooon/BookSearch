package com.easyhooon.booksearch.core.data.repository

import com.easyhooon.booksearch.core.data.mapper.toEntity
import com.easyhooon.booksearch.core.data.mapper.toModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DefaultBookRepository @Inject constructor(
    private val favoritesDao: FavoritesDao,
) : BookRepository {

    override fun searchFavoritesByTitle(query: String) = favoritesDao.searchFavoritesByTitle(query)
        .map { entities -> entities.map { it.toModel() } }

    override suspend fun insertBook(book: Book) {
        favoritesDao.insertFavorite(book.toEntity())
    }

    override suspend fun deleteBook(isbn: String) {
        favoritesDao.deleteFavorite(isbn)
    }

    override val favoriteBooks: Flow<List<Book>> = favoritesDao.getAllFavorites()
        .map { entities -> entities.map { it.toModel() } }
}
