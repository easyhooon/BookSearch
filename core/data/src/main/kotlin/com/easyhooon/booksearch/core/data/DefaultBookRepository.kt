package com.easyhooon.booksearch.core.data

import com.easyhooon.booksearch.core.data.mapper.toEntity
import com.easyhooon.booksearch.core.data.mapper.toModel
import com.easyhooon.booksearch.core.data.util.cancellableRunCatching
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.network.service.BookSearchService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DefaultBookRepository @Inject constructor(
    private val service: BookSearchService,
    private val favoritesDao: FavoritesDao,
) : BookRepository {

    override suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ) = cancellableRunCatching {
        service.searchBook(
            query = query,
            sort = sort,
            page = page,
            size = size,
        ).toModel()
    }

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
