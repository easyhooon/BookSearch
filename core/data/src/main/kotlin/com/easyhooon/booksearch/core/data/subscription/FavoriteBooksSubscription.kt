package com.easyhooon.booksearch.core.data.subscription

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import com.orhanobut.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import soil.query.SubscriptionId
import soil.query.SubscriptionKey
import soil.query.buildSubscriptionKey

typealias FavoriteBooksSubscriptionKey = SubscriptionKey<List<BookUiModel>>

@Singleton
class DefaultFavoriteBooksSubscriptionKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(
        query: String = "",
        sortType: String = "LATEST",
        isPriceFilterEnabled: Boolean = false,
    ): FavoriteBooksSubscriptionKey = buildSubscriptionKey(
        id = SubscriptionId("favorite_books_subscription_${query}_${sortType}_$isPriceFilterEnabled"),
        subscribe = { subscribeFavorites(query, sortType, isPriceFilterEnabled) }
    )

    private fun subscribeFavorites(
        query: String,
        sortType: String,
        isPriceFilterEnabled: Boolean,
    ): Flow<List<BookUiModel>> {
        Logger.d("subscribeFavorites() called - query: $query, sortType: $sortType, isPriceFilterEnabled: $isPriceFilterEnabled")

        return if (query.isNotEmpty()) {
            favoritesDao.searchFavoritesByTitle(query)
        } else {
            favoritesDao.getAllFavorites()
        }.map { books ->
            Logger.d("FavoriteBooks Raw data from DB: ${books.size} books")
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
            val result = bookEntities.map { bookEntity ->
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
            Logger.d("FavoriteBooks Flow mapping result: ${result.size} books")
            result
        }
    }
}
