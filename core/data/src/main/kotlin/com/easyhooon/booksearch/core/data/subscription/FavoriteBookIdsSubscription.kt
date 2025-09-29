package com.easyhooon.booksearch.core.data.subscription

import com.easyhooon.booksearch.core.database.FavoritesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import soil.query.SubscriptionId
import soil.query.SubscriptionKey
import soil.query.SubscriptionReceiver
import javax.inject.Inject
import javax.inject.Singleton

typealias FavoriteBookIdsSubscriptionKey = SubscriptionKey<Set<String>>

@Singleton
class DefaultFavoriteBookIdsSubscriptionKey @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    fun create(): FavoriteBookIdsSubscriptionKey = object : SubscriptionKey<Set<String>> {
        override val id: SubscriptionId<Set<String>> = SubscriptionId(
            namespace = "favorite_book_ids_subscription",
            tags = emptyArray(),
        )

        override val subscribe: SubscriptionReceiver.() -> Flow<Set<String>>
            get() = { subscribeFavoriteBookIds() }
    }

    private fun subscribeFavoriteBookIds(): Flow<Set<String>> {
        return favoritesDao.getAllFavorites().map { books ->
            books.map { it.isbn }.toSet()
        }
    }
}
