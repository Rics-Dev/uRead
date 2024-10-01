package com.ricdev.uread.domain.use_case.annotations

import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddAnnotationUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(annotation: BookAnnotation): Long = withContext(Dispatchers.IO) {
        repository.addAnnotation(annotation)
    }
}