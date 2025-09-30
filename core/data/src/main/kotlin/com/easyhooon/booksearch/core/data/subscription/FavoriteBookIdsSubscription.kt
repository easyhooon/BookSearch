package com.easyhooon.booksearch.core.data.subscription

import com.easyhooon.booksearch.core.database.FavoritesDao
import com.orhanobut.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import soil.query.SubscriptionContentEquals
import soil.query.SubscriptionId
import soil.query.SubscriptionKey
import soil.query.SubscriptionReceiver

typealias FavoriteBookIdsSubscriptionKey = SubscriptionKey<Set<String>>

@Singleton
class DefaultFavoriteBookIdsSubscriptionKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(): FavoriteBookIdsSubscriptionKey = object : SubscriptionKey<Set<String>> {
        override val id = SubscriptionId<Set<String>>("favorite_book_ids_subscription")
        override val subscribe: SubscriptionReceiver.() -> Flow<Set<String>> = {
            Logger.d("subscribeFavoriteBookIds() called")
            favoritesDao.getAllFavorites().map { books ->
                val result = books.map { it.isbn }.toSet()
                Logger.d("FavoriteBookIds Flow emitted: $result (size: ${result.size}) timestamp: ${System.currentTimeMillis()}")
                result
            }
        }
        // override val contentEquals: SubscriptionContentEquals<Set<String>> = { old: Set<String>, new: Set<String> -> false }
    }
}
