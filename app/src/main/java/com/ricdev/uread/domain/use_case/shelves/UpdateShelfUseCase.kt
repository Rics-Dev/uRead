package com.ricdev.uread.domain.use_case.shelves

import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.domain.repository.ShelfRepository
import javax.inject.Inject

class UpdateShelfUseCase @Inject constructor(
    private val shelfRepository: ShelfRepository
) {
    suspend operator fun invoke(shelf: Shelf) {
        shelfRepository.updateShelf(shelf)
    }
}