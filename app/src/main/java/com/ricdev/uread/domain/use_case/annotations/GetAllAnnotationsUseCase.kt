package com.ricdev.uread.domain.use_case.annotations

import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAnnotationsUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(): Flow<List<BookAnnotation>> {
        return repository.getAllAnnotations()
    }
}