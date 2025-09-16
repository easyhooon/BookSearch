package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.network.client.BookSearchKtorClient
import soil.query.InfiniteQueryId
import soil.query.InfiniteQueryKey
import soil.query.QueryChunks
import soil.query.QueryReceiver
import soil.query.QueryId
import soil.query.QueryKey
import javax.inject.Inject
import javax.inject.Singleton
data class SearchBooksPageParam(
    val page: Int
)

data class SearchBooksQueryKey(
    val query: String,
    val sort: String = "accuracy",
    val size: Int = 20,
) : InfiniteQueryKey<List<BookUiModel>, SearchBooksPageParam> {

    override val id: InfiniteQueryId<List<BookUiModel>, SearchBooksPageParam> = InfiniteQueryId(
        namespace = "search_books",
        tags = arrayOf("$query:$sort:$size")
    )
    
    override val initialParam: () -> SearchBooksPageParam = { SearchBooksPageParam(1) }
    
    override val fetch: suspend QueryReceiver.(pageParam: SearchBooksPageParam) -> List<BookUiModel>
        get() = { pageParam -> SearchBooksQuery.instance.fetch(this@SearchBooksQueryKey, pageParam) }
        
    override val loadMoreParam: (QueryChunks<List<BookUiModel>, SearchBooksPageParam>) -> SearchBooksPageParam? = { chunks ->
        val pageSize = size
        val lastChunk = chunks.lastOrNull()
        val lastPageData = lastChunk?.data
        if (lastPageData != null && lastPageData.size == pageSize) {
            SearchBooksPageParam(chunks.size + 1)
        } else {
            null
        }
    }
}

// Presenter에서 사용할 단일 페이지 QueryKey
data class SearchBooksPageQueryKey(
    val query: String,
    val sort: String = "accuracy",
    val page: Int = 1,
    val size: Int = 20,
) : QueryKey<List<BookUiModel>> {
    
    override val id: QueryId<List<BookUiModel>> = QueryId(
        namespace = "search_books_page",
        tags = arrayOf("$query:$sort:$page:$size")
    )
    
    override val fetch: suspend QueryReceiver.() -> List<BookUiModel>
        get() = { SearchBooksQuery.instance.fetchPage(this@SearchBooksPageQueryKey) }
}

@Singleton  
class SearchBooksQuery @Inject constructor(
    private val ktorClient: BookSearchKtorClient,
) {
    suspend fun fetch(key: SearchBooksQueryKey, pageParam: SearchBooksPageParam): List<BookUiModel> {
        // Skip empty queries
        if (key.query.isBlank()) return emptyList()
        
        val response = ktorClient.searchBook(
            query = key.query,
            sort = key.sort,
            page = pageParam.page,
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
    
    suspend fun fetchPage(key: SearchBooksPageQueryKey): List<BookUiModel> {
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
