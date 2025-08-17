package com.easyhooon.booksearch.core.data.mapper

import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.model.Meta
import com.easyhooon.booksearch.core.domain.model.SearchBook
import com.easyhooon.booksearch.core.network.response.BookResponse
import com.easyhooon.booksearch.core.network.response.MetaResponse
import com.easyhooon.booksearch.core.network.response.SearchBookResponse

internal fun SearchBookResponse.toModel(): SearchBook {
    return SearchBook(
        meta = meta.toModel(),
        documents = documents.map { it.toModel() },
    )
}

internal fun MetaResponse.toModel(): Meta {
    return Meta(
        pageableCount = pageableCount,
        totalCount = totalCount,
        isEnd = isEnd,
    )
}

internal fun BookResponse.toModel(): Book {
    return Book(
        title = title,
        contents = contents,
        url = url,
        isbn = isbn,
        datetime = datetime,
        authors = authors,
        publisher = publisher,
        translators = translators,
        price = price,
        salePrice = salePrice,
        thumbnail = thumbnail,
        status = status,
    )
}
