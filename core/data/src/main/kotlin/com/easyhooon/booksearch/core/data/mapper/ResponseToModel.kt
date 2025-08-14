package com.easyhooon.booksearch.core.data.mapper

import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.network.response.BookResponse

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
        status = status
    )
}