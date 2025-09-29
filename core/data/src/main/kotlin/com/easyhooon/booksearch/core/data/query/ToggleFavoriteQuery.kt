package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.database.entity.BookEntity
import com.orhanobut.logger.Logger
import soil.query.MutationId
import soil.query.MutationKey
import soil.query.MutationReceiver
import soil.query.buildMutationKey
import javax.inject.Inject
import javax.inject.Singleton

typealias ToggleFavoriteQueryKey = MutationKey<Boolean, Unit>

@Singleton  
class DefaultToggleFavoriteQueryKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(book: BookUiModel): ToggleFavoriteQueryKey = buildMutationKey(
        id = MutationId("toggle_favorite_${book.isbn}"),
        mutate = { toggleFavorite(book) }
    )

    private suspend fun toggleFavorite(book: BookUiModel): Boolean {
        Logger.d("toggleFavorite called for book: ${book.title} (${book.isbn}), current isFavorites: ${book.isFavorites}")

        val bookEntity = BookEntity(
            isbn = book.isbn,
            title = book.title,
            contents = book.contents,
            url = book.url,
            datetime = book.datetime,
            authors = book.authors,
            publisher = book.publisher,
            translators = book.translators,
            price = book.price,
            salePrice = book.salePrice,
            thumbnail = book.thumbnail,
            status = book.status,
        )

        // Simple toggle - if it's favorite, remove it; if not, add it
        return if (book.isFavorites) {
            Logger.d("Removing from favorites: ${book.isbn}")
            val deletedRows = favoritesDao.deleteFavorite(book.isbn)
            Logger.d("Delete operation completed for: ${book.isbn}, deleted rows: $deletedRows")
            false // Successfully removed
        } else {
            Logger.d("Adding to favorites: ${book.isbn}")
            favoritesDao.insertFavorite(bookEntity)
            Logger.d("Insert operation completed for: ${book.isbn}")
            true // Successfully added
        }
    }
}
