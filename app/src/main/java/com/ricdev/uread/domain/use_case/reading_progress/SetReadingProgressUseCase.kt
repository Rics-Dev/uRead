package com.ricdev.uread.domain.use_case.reading_progress

import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class SetReadingProgressUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(bookId: Long, locator: String) = withContext(Dispatchers.IO) {
        val progression = getProgressionFromLocator(locator)


        updateReadingStatus(bookId, progression)

        repository.setReadingProgress(bookId, locator, progression)

    }

    private suspend fun updateReadingStatus(bookId: Long, progression: Float) {
        when {
            progression >= 99f -> {
                repository.setReadingStatus(bookId, ReadingStatus.FINISHED)
            }
            progression > 2f -> repository.setReadingStatus(bookId, ReadingStatus.IN_PROGRESS)
        }
    }

    private fun getProgressionFromLocator(locatorJson: String): Float {
        return try {
            val locator = JSONObject(locatorJson)
            val locations = locator.optJSONObject("locations")
            (locations?.optDouble("totalProgression", 0.0)?.toFloat() ?: 0f) * 100f
        } catch (e: Exception) {
            0f
        }
    }
}
