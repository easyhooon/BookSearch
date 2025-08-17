package com.easyhooon.booksearch.core.common.mapper

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.domain.model.Book

fun Book.toUiModel(): BookUiModel {
    return BookUiModel(
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
        isFavorites = isFavorite,
    )
}
