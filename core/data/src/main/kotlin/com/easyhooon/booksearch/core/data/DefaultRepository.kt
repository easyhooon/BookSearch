package com.easyhooon.booksearch.core.data

import com.easyhooon.booksearch.core.data.mapper.toEntity
import com.easyhooon.booksearch.core.data.util.cancellableRunCatching
import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.network.service.BookSearchService
import javax.inject.Inject

internal class DefaultRepository @Inject constructor(
    private val service: BookSearchService,
) : BookRepository {
    override suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ) = cancellableRunCatching {
        service.searchBook(
            query = query,
            sort = sort,
            page = page,
            size = size
        ).documents.map { it.toEntity() }
    }
}