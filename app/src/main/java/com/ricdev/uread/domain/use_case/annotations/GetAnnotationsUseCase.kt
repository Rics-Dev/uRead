package com.ricdev.uread.domain.use_case.annotations

import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnnotationsUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(bookId: Long): Flow<List<BookAnnotation>> {
        return repository.getAnnotations(bookId)
    }
}