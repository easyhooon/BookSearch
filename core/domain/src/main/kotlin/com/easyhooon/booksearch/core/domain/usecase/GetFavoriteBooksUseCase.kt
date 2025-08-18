package com.easyhooon.booksearch.core.domain.usecase

import com.easyhooon.booksearch.core.domain.BookRepository
import com.easyhooon.booksearch.core.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteBooksUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(query: String, sortType: FavoritesSortType): Flow<List<Book>> {
        val booksFlow = if (query.isBlank()) {
            repository.favoriteBooks
        } else {
            repository.searchFavoritesByTitle(query)
        }

        return booksFlow.map { books ->
            when (sortType) {
                FavoritesSortType.TITLE_ASC -> books.sortedBy { it.title }
                FavoritesSortType.TITLE_DESC -> books.sortedByDescending { it.title }
            }
        }
    }
}

enum class FavoritesSortType(val label: String) {
    TITLE_ASC("오름차순(제목)"),
    TITLE_DESC("내림차순(제목)");

    fun toggle(): FavoritesSortType {
        return when (this) {
            TITLE_ASC -> TITLE_DESC
            TITLE_DESC -> TITLE_ASC
        }
    }
}