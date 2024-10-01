package com.ricdev.uread.domain.use_case.annotations

import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class UpdateAnnotationUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(annotation: BookAnnotation) {
        repository.updateAnnotation(annotation)
    }
}