package com.ricdev.uread.domain.use_case.shelves

import com.ricdev.uread.domain.repository.ShelfRepository
import javax.inject.Inject

class AddBookToShelfUseCase @Inject constructor(private val shelfRepository: ShelfRepository) {
    suspend operator fun invoke(bookId: Long, shelfId: Long) {
        shelfRepository.addBookToShelf(bookId, shelfId)
    }
}