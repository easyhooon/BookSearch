package com.easyhooon.booksearch.core.data.mapper

import com.easyhooon.booksearch.core.database.entity.BookEntity
import com.easyhooon.booksearch.core.domain.model.Book

internal fun BookEntity.toModel(): Book {
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
