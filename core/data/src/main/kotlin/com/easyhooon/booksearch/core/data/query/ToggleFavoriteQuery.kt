package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.database.entity.BookEntity
import soil.query.MutationId
import soil.query.MutationKey
import soil.query.MutationReceiver
import javax.inject.Inject
import javax.inject.Singleton

typealias ToggleFavoriteQueryKey = MutationKey<Boolean, Unit>

@Singleton
class DefaultToggleFavoriteQueryKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(book: BookUiModel): ToggleFavoriteQueryKey = object : MutationKey<Boolean, Unit> {
        override val id: MutationId<Boolean, Unit> = MutationId(
            namespace = "toggle_favorite",
            tags = arrayOf(book.isbn)
        )

        override val mutate: suspend MutationReceiver.(variable: Unit) -> Boolean
            get() = { toggleFavorite(book) }
    }
    
    private suspend fun toggleFavorite(book: BookUiModel): Boolean {
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
            favoritesDao.deleteFavorite(book.isbn)
            false // Successfully removed
        } else {
            favoritesDao.insertFavorite(bookEntity)
            true // Successfully added
        }
    }

}
