package com.ricdev.uread.domain.use_case.shelves

import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// GetShelvesUseCase.kt
class GetShelvesUseCase @Inject constructor(private val shelfRepository: ShelfRepository) {
    operator fun invoke(): Flow<List<Shelf>> = shelfRepository.getShelves()
}