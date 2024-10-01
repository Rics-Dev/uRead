package com.ricdev.uread.domain.use_case.bookmarks

import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksForBookUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(bookId: Long): Flow<List<Bookmark>> {
        return repository.getBookmarksForBook(bookId)
    }
}