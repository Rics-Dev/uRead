package com.ricdev.uread.domain.use_case.books

import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBookUrisUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(): List<String> = withContext(Dispatchers.IO) {
        repository.getAllBookUris()
    }
}