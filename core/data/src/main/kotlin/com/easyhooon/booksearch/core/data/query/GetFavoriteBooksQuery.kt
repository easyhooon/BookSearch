package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.database.FavoritesDao
import kotlinx.coroutines.flow.first
import soil.query.QueryId
import soil.query.QueryKey
import soil.query.QueryReceiver
import javax.inject.Inject
import javax.inject.Singleton

data class GetFavoriteBooksQueryKey(
    val query: String = "",
    val sortType: String = "LATEST",
    val isPriceFilterEnabled: Boolean = false,
) : QueryKey<List<BookUiModel>> {

    override val id: QueryId<List<BookUiModel>> = QueryId(
        namespace = "favorite_books",
        tags = arrayOf("$query:$sortType:$isPriceFilterEnabled")
    )
    
    override val fetch: suspend QueryReceiver.() -> List<BookUiModel>
        get() = { GetFavoriteBooksQuery.instance.fetch(this@GetFavoriteBooksQueryKey) }
}

@Singleton
class GetFavoriteBooksQuery @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    suspend fun fetch(key: GetFavoriteBooksQueryKey): List<BookUiModel> {
        return if (key.query.isNotEmpty()) {
            favoritesDao.searchFavoritesByTitle(key.query).first()
        } else {
            favoritesDao.getAllFavorites().first()
        }.let { books ->
                var filteredBooks = books
                
                // Apply price filter (if enabled, filter out books with price = 0 or empty)
                if (key.isPriceFilterEnabled) {
                    filteredBooks = filteredBooks.filter { 
                        it.price.isNotEmpty() && it.price != "0" 
                    }
                }
                
                // Apply sorting
                when (key.sortType) {
                    "LATEST" -> filteredBooks.sortedByDescending { it.datetime }
                    "OLDEST" -> filteredBooks.sortedBy { it.datetime }
                    "PRICE_LOW_TO_HIGH" -> filteredBooks.sortedBy { it.price.toIntOrNull() ?: 0 }
                    "PRICE_HIGH_TO_LOW" -> filteredBooks.sortedByDescending { it.price.toIntOrNull() ?: 0 }
                    else -> filteredBooks
                }
            }.map { bookEntity ->
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
                    isFavorites = true, // All favorites are favorite by definition
                )
            }
    }

    companion object {
        lateinit var instance: GetFavoriteBooksQuery
    }
}