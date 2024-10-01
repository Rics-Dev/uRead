package com.ricdev.uread.domain.use_case.reading_activity

import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class GetReadingActivityByDateUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(date: Long) = repository.getReadingActivityByDate(date)
}