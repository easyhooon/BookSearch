package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.easyhooon.booksearch.core.database.entity.BookEntity
import soil.query.MutationId
import soil.query.MutationKey
import soil.query.MutationReceiver
import javax.inject.Inject
import javax.inject.Singleton

data class ToggleFavoriteQueryKey(
    val book: BookUiModel,
) : MutationKey<Boolean, Unit> {

    override val id: MutationId<Boolean, Unit> = MutationId(
        namespace = "toggle_favorite",
        tags = arrayOf("${book.isbn}")
    )
    
    override val mutate: suspend MutationReceiver.(variable: Unit) -> Boolean
        get() = { ToggleFavoriteQuery.instance.fetch(this@ToggleFavoriteQueryKey) }
}

@Singleton
class ToggleFavoriteQuery @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    suspend fun fetch(key: ToggleFavoriteQueryKey): Boolean {
        val bookEntity = BookEntity(
            isbn = key.book.isbn,
            title = key.book.title,
            contents = key.book.contents,
            url = key.book.url,
            datetime = key.book.datetime,
            authors = key.book.authors,
            publisher = key.book.publisher,
            translators = key.book.translators,
            price = key.book.price,
            salePrice = key.book.salePrice,
            thumbnail = key.book.thumbnail,
            status = key.book.status,
        )
        
        // Simple toggle - if it's favorite, remove it; if not, add it
        return if (key.book.isFavorites) {
            favoritesDao.deleteFavorite(key.book.isbn)
            false // Successfully removed
        } else {
            favoritesDao.insertFavorite(bookEntity)
            true // Successfully added
        }
    }

    companion object {
        lateinit var instance: ToggleFavoriteQuery
    }
}