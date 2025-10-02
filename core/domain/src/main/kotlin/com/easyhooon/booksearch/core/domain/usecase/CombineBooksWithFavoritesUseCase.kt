package com.easyhooon.booksearch.core.domain.usecase

import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CombineBooksWithFavoritesUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(booksFlow: Flow<List<Book>>): Flow<List<Book>> {
        return combine(
            booksFlow,
            repository.favoriteBooks,
        ) { books, favoriteBooks ->
            books.map { book ->
                val isFavorite = favoriteBooks.any { it.isbn == book.isbn }
                book.copy(isFavorite = isFavorite)
            }
        }
    }
}
