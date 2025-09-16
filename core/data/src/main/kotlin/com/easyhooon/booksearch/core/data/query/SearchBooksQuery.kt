package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.network.client.BookSearchKtorClient
import soil.query.QueryId
import soil.query.QueryKey
import javax.inject.Inject
import javax.inject.Singleton
import soil.query.QueryReceiver
data class SearchBooksQueryKey(
    val query: String,
    val sort: String = "accuracy",
    val page: Int = 1,
    val size: Int = 20,
) : QueryKey<List<BookUiModel>> {

    override val id: QueryId<List<BookUiModel>> = QueryId(
        namespace = "search_books",
        tags = arrayOf("$query:$sort:$page:$size")
    )
    
    override val fetch: suspend QueryReceiver.() -> List<BookUiModel>
        get() = { SearchBooksQuery.instance.fetch(this@SearchBooksQueryKey) }
}

@Singleton  
class SearchBooksQuery @Inject constructor(
    private val ktorClient: BookSearchKtorClient,
) {
    suspend fun fetch(key: SearchBooksQueryKey): List<BookUiModel> {
        // Skip empty queries
        if (key.query.isBlank()) return emptyList()
        
        val response = ktorClient.searchBook(
            query = key.query,
            sort = key.sort,
            page = key.page,
            size = key.size
        )

        return response.documents.map { bookResponse ->
            BookUiModel(
                isbn = bookResponse.isbn,
                title = bookResponse.title,
                contents = bookResponse.contents,
                url = bookResponse.url,
                datetime = bookResponse.datetime,
                authors = bookResponse.authors,
                publisher = bookResponse.publisher,
                translators = bookResponse.translators,
                price = bookResponse.price,
                salePrice = bookResponse.salePrice,
                thumbnail = bookResponse.thumbnail,
                status = bookResponse.status,
                isFavorites = false, // TODO: Check favorites status
            )
        }
    }

    companion object {
        lateinit var instance: SearchBooksQuery
    }
}
