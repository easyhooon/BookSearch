package com.easyhooon.booksearch.core.data.query

import android.util.Log
import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.network.client.BookSearchKtorClient
import javax.inject.Inject
import javax.inject.Singleton
import soil.query.InfiniteQueryId
import soil.query.InfiniteQueryKey
import soil.query.buildInfiniteQueryKey

data class SearchBooksPageParam(
    val page: Int,
)

@Singleton
class DefaultSearchBooksQueryKey @Inject constructor(
    private val ktorClient: BookSearchKtorClient,
) {
    fun create(
        query: String,
        sort: String = "accuracy",
        size: Int = 20,
    ): InfiniteQueryKey<List<BookUiModel>, SearchBooksPageParam> = buildInfiniteQueryKey(
        id = InfiniteQueryId(
            namespace = "search_books",
            tags = arrayOf("$query:$sort:$size"),
        ),
        initialParam = { SearchBooksPageParam(1) },
        fetch = { pageParam ->
            Log.d("SearchBooksQuery", "fetch called with query='$query', sort='$sort', page=${pageParam.page}, size=$size")

            if (query.isBlank()) {
                Log.d("SearchBooksQuery", "Query is blank, returning empty list")
                emptyList()
            } else {
                Log.d("SearchBooksQuery", "Making API call to searchBook")
                val response = ktorClient.searchBook(
                    query = query,
                    sort = sort,
                    page = pageParam.page,
                    size = size,
                )

                Log.d("SearchBooksQuery", "API response received: ${response.documents.size} documents")

                response.documents.map { bookResponse ->
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
                        isFavorites = false,
                    )
                }
            }
        },
        loadMoreParam = { chunks ->
            val lastChunk = chunks.lastOrNull()
            val lastPageData = lastChunk?.data
            if (lastPageData != null && lastPageData.size == size) {
                SearchBooksPageParam(chunks.size + 1)
            } else {
                null
            }
        },
    )
}
