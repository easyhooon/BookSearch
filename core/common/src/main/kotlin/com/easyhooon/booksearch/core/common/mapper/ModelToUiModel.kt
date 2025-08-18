package com.easyhooon.booksearch.core.common.mapper

import com.easyhooon.booksearch.core.common.model.BookUiModel
import com.easyhooon.booksearch.core.domain.model.Book
import kotlinx.collections.immutable.toImmutableList

fun Book.toUiModel(): BookUiModel {
    return BookUiModel(
        title = title,
        contents = contents,
        url = url,
        isbn = isbn,
        datetime = datetime,
        authors = authors.toImmutableList(),
        publisher = publisher,
        translators = translators.toImmutableList(),
        price = price,
        salePrice = salePrice,
        thumbnail = thumbnail,
        status = status,
        isFavorites = isFavorite,
    )
}
