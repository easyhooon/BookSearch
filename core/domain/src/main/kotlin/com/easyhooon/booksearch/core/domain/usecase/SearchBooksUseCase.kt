package com.easyhooon.booksearch.core.domain.usecase

import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.util.cancellableRunCatching
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(
        query: String,
        sort: String,
        page: Int,
        size: Int,
        currentBooks: List<Book> = emptyList(),
        isFirstPage: Boolean = true,
    ): Result<SearchResult> {
        return cancellableRunCatching {
            val searchResult = repository.searchBook(query, sort, page, size)

            val newBooks = if (isFirstPage) {
                searchResult.documents
            } else {
                currentBooks + searchResult.documents
            }

            SearchResult(
                books = newBooks,
                isEnd = searchResult.meta.isEnd,
                totalCount = searchResult.meta.totalCount,
                nextPage = page + 1,
            )
        }
    }
}

data class SearchResult(
    val books: List<Book>,
    val isEnd: Boolean,
    val totalCount: Int,
    val nextPage: Int,
)
