package com.ricdev.uread.domain.use_case.shelves

import com.ricdev.uread.domain.repository.ShelfRepository
import javax.inject.Inject

class GetBooksForShelfUseCase @Inject constructor(private val shelfRepository: ShelfRepository) {
    operator fun invoke(shelfId: Long) = shelfRepository.getBooksForShelf(shelfId)
}