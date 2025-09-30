package com.easyhooon.booksearch.core.data.query

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.network.client.BookSearchKtorClient
import com.orhanobut.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton
import soil.query.InfiniteQueryId
import soil.query.InfiniteQueryKey
import soil.query.QueryChunks
import soil.query.QueryOptionsOverride
import soil.query.QueryReceiver
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import soil.query.copy

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
    ): InfiniteQueryKey<List<BookUiModel>, SearchBooksPageParam> = object : InfiniteQueryKey<List<BookUiModel>, SearchBooksPageParam> {
        override val id = InfiniteQueryId<List<BookUiModel>, SearchBooksPageParam>("search_books_${query}_${sort}_$size")
        override val initialParam = { SearchBooksPageParam(1) }
        override val fetch: suspend QueryReceiver.(param: SearchBooksPageParam) -> List<BookUiModel> = { pageParam ->
            Logger.d("SearchBooksQuery: fetch called with query='$query', sort='$sort', page=${pageParam.page}, size=$size")

            if (query.isBlank()) {
                Logger.d("SearchBooksQuery: Query is blank, returning empty list")
                emptyList()
            } else {
                Logger.d("SearchBooksQuery: Making API call to searchBook")
                val response = ktorClient.searchBook(
                    query = query,
                    sort = sort,
                    page = pageParam.page,
                    size = size,
                )

                Logger.d("SearchBooksQuery: API response received: ${response.documents.size} documents")

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
                        isFavorites = null,
                    )
                }
            }
        }
        override val loadMoreParam = { chunks: QueryChunks<List<BookUiModel>, SearchBooksPageParam> ->
            val lastChunk = chunks.lastOrNull()
            val lastPageData = lastChunk?.data
            if (lastPageData != null && lastPageData.size == size) {
                SearchBooksPageParam(chunks.size + 1)
            } else {
                null
            }
        }
        override fun onConfigureOptions(): QueryOptionsOverride = { options ->
            options.copy(
                staleTime = 30.seconds, // 30초간 fresh 상태 유지 (API 호출 안함)
                gcTime = 10.minutes     // 10분간 메모리 캐시 유지
            )
        }
    }
}
