package com.ricdev.uread.domain.use_case.reading_activity

import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class AddReadingActivityUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(readingActivity: ReadingActivity) {
        repository.insertOrUpdateReadingActivity(readingActivity)
    }
}