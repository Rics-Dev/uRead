package com.ricdev.uread.domain.use_case.reading_activity

import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllReadingActivitiesUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(): Flow<List<ReadingActivity>> {
        return repository.getAllReadingActivities()
    }
}