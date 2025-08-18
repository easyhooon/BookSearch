package com.easyhooon.booksearch.core.domain.usecase

import com.easyhooon.booksearch.core.domain.model.Book
import com.easyhooon.booksearch.core.domain.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(book: Book): Boolean {
        val favoriteBooks = repository.favoriteBooks.first()
        val isCurrentlyFavorite = favoriteBooks.any { it.isbn == book.isbn }

        return if (isCurrentlyFavorite) {
            repository.deleteBook(book.isbn)
            false
        } else {
            repository.insertBook(book)
            true
        }
    }
}
