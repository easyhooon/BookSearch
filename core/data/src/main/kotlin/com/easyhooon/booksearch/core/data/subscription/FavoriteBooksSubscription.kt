package com.easyhooon.booksearch.core.data.subscription

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import soil.query.SubscriptionId
import soil.query.SubscriptionKey
import soil.query.SubscriptionReceiver
import javax.inject.Inject
import javax.inject.Singleton

typealias FavoriteBooksSubscriptionKey = SubscriptionKey<List<BookUiModel>>

@Singleton
class DefaultFavoriteBooksSubscriptionKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(
        query: String = "",
        sortType: String = "LATEST",
        isPriceFilterEnabled: Boolean = false,
    ): FavoriteBooksSubscriptionKey = object : SubscriptionKey<List<BookUiModel>> {
        override val id: SubscriptionId<List<BookUiModel>> = SubscriptionId(
            namespace = "favorite_books_subscription",
            tags = arrayOf("$query:$sortType:$isPriceFilterEnabled"),
        )

        override val subscribe: SubscriptionReceiver.() -> Flow<List<BookUiModel>>
            get() = { subscribeFavorites(query, sortType, isPriceFilterEnabled) }
    }

    private fun subscribeFavorites(
        query: String,
        sortType: String,
        isPriceFilterEnabled: Boolean,
    ): Flow<List<BookUiModel>> {
        return if (query.isNotEmpty()) {
            favoritesDao.searchFavoritesByTitle(query)
        } else {
            favoritesDao.getAllFavorites()
        }.map { books ->
            var filteredBooks = books

            if (isPriceFilterEnabled) {
                filteredBooks = filteredBooks.filter {
                    it.price.isNotEmpty() && it.price != "0"
                }
            }

            when (sortType) {
                "LATEST" -> filteredBooks.sortedByDescending { it.datetime }
                "OLDEST" -> filteredBooks.sortedBy { it.datetime }
                "PRICE_LOW_TO_HIGH" -> filteredBooks.sortedBy { it.price.toIntOrNull() ?: 0 }
                "PRICE_HIGH_TO_LOW" -> filteredBooks.sortedByDescending { it.price.toIntOrNull() ?: 0 }
                else -> filteredBooks
            }
        }.map { bookEntities ->
            bookEntities.map { bookEntity ->
                BookUiModel(
                    isbn = bookEntity.isbn,
                    title = bookEntity.title,
                    contents = bookEntity.contents,
                    url = bookEntity.url,
                    datetime = bookEntity.datetime,
                    authors = bookEntity.authors,
                    publisher = bookEntity.publisher,
                    translators = bookEntity.translators,
                    price = bookEntity.price,
                    salePrice = bookEntity.salePrice,
                    thumbnail = bookEntity.thumbnail,
                    status = bookEntity.status,
                    isFavorites = true,
                )
            }
        }
    }
}
