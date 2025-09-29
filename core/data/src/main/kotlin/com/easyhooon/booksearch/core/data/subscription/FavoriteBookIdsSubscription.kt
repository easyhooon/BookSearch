package com.easyhooon.booksearch.core.data.subscription

import com.easyhooon.booksearch.core.database.FavoritesDao
import com.orhanobut.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import soil.query.SubscriptionId
import soil.query.SubscriptionKey
import soil.query.buildSubscriptionKey

typealias FavoriteBookIdsSubscriptionKey = SubscriptionKey<Set<String>>

@Singleton
class DefaultFavoriteBookIdsSubscriptionKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(): FavoriteBookIdsSubscriptionKey = buildSubscriptionKey(
        id = SubscriptionId("favorite_book_ids_subscription"),
        subscribe = { 
            Logger.d("subscribeFavoriteBookIds() called")
            favoritesDao.getAllFavorites().map { books ->
                val result = books.map { it.isbn }.toSet()
                Logger.d("FavoriteBookIds Flow emitted: $result")
                result
            }
        }
    )
}
