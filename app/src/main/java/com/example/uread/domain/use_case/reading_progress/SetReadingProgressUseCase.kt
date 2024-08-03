package com.example.uread.domain.use_case.reading_progress

import com.example.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetReadingProgressUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(uri: String, locator: String) = withContext(Dispatchers.IO) {
        repository.setReadingProgress(uri, locator)
    }
}