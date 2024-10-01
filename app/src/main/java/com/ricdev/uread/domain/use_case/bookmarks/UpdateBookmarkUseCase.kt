package com.ricdev.uread.domain.use_case.bookmarks

import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(bookmark: Bookmark) {
        repository.updateBookmark(bookmark)
    }
}