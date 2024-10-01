package com.ricdev.uread.domain.use_case.shelves

import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.domain.repository.ShelfRepository
import javax.inject.Inject

// AddShelfUseCase.kt
class AddShelfUseCase @Inject constructor(private val shelfRepository: ShelfRepository) {
    suspend operator fun invoke(shelfName: String, order: Int): Long {
        return shelfRepository.addShelf(Shelf(name = shelfName, order = order))
    }
}