package com.ricdev.uread.presentation.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ricdev.uread.data.model.AnnotationType
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.domain.model.Author
import com.ricdev.uread.domain.model.Genre
import com.ricdev.uread.domain.model.Statistics
import com.ricdev.uread.domain.use_case.annotations.GetAllAnnotationsUseCase
import com.ricdev.uread.domain.use_case.books.GetAllBooksUseCase
import com.ricdev.uread.domain.use_case.notes.GetAllNotesUseCase
import com.ricdev.uread.domain.use_case.reading_activity.GetAllReadingActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllAnnotationsUseCase: GetAllAnnotationsUseCase,
    private val getAllReadingActivitiesUseCase: GetAllReadingActivitiesUseCase,
    application: Application,
) : AndroidViewModel(application) {

    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()


    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()


    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _annotations = MutableStateFlow<List<BookAnnotation>>(emptyList())
    val annotations: StateFlow<List<BookAnnotation>> = _annotations.asStateFlow()


    private val _readingActivities = MutableStateFlow<List<ReadingActivity>>(emptyList())
//    val readingActivities: StateFlow<List<ReadingActivity>> = _readingActivities.asStateFlow()



    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            combine(
                getAllBooksUseCase(),
                getAllNotesUseCase(),
                getAllAnnotationsUseCase(),
                getAllReadingActivitiesUseCase(),
            ) { books, notes, annotations, readingActivities ->
                _books.value = books
                _notes.value = notes
                _annotations.value = annotations
                _readingActivities.value = readingActivities
                calculateStatistics(books, notes, annotations,readingActivities )
            }.collect { calculatedStatistics ->
                _statistics.value = calculatedStatistics
            }
        }
    }


    private fun calculateStatistics(books: List<Book>, notes: List<Note>, annotations: List<BookAnnotation>, readingActivities: List<ReadingActivity> ): Statistics {
        val currentDate = LocalDate.now()
        val currentYear = currentDate.year
        val currentMonth = currentDate.monthValue

        val booksReadThisYear = books.count { book ->
            book.readingStatus == ReadingStatus.FINISHED &&
                    book.endReadingDate?.let { endDate ->
                        val endReadingDate =
                            Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        endReadingDate.year == currentYear
                    } ?: false
        }

        val booksReadThisMonth = books.count { book ->
            book.readingStatus == ReadingStatus.FINISHED &&
                    book.endReadingDate?.let { endDate ->
                        val endReadingDate =
                            Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        endReadingDate.year == currentYear && endReadingDate.monthValue == currentMonth
                    } ?: false
        }


        val totalReadingTime = readingActivities.sumOf { it.readingTime }
        val averageReadingTimePerBook = if (books.isNotEmpty()) totalReadingTime / books.size else 0
        val averageDailyReadingTime = if (readingActivities.isNotEmpty()) {
            readingActivities.sumOf { it.readingTime } / readingActivities.size
        } else {
            0
        }


        // Sort reading activities by date
        val sortedActivities = readingActivities.sortedBy { it.date }

        // calculate reading streaks
        val (longestStreak, currentStreak) = calculateReadingStreaks(sortedActivities, currentDate)





        return Statistics(
            totalBooks = books.size,
            booksRead = books.count { it.readingStatus == ReadingStatus.FINISHED },
            booksReadThisYear = booksReadThisYear,
            booksReadThisMonth = booksReadThisMonth,
            booksInProgress = books.count { it.readingStatus == ReadingStatus.IN_PROGRESS },
            booksToRead = books.count { it.readingStatus == ReadingStatus.NOT_STARTED },
            totalReadingTime = totalReadingTime,
            averageReadingTimePerBook = averageReadingTimePerBook,
            averageDailyReadingTime = averageDailyReadingTime,
            longestReadingStreak = longestStreak,
            currentReadingStreak = currentStreak,
            favoriteBooks = books.count { it.isFavorite },
            ratedBooks = books.count { it.rating > 0 },
            averageRating = books.filter { it.rating > 0 }.let { ratedBooks ->
                if (ratedBooks.isNotEmpty()) ratedBooks.sumOf { it.rating.toDouble() } / ratedBooks.size else 0.0
            },

            totalNotes = notes.size,
            totalHighlights = annotations.count { it.type == AnnotationType.HIGHLIGHT },
            totalUnderlines = annotations.count { it.type == AnnotationType.UNDERLINE },

            favoriteAuthors = books.groupBy { it.authors }.map { (author, books) ->
                Author(name = author, books = books)
            }.sortedByDescending { it.books.size },



            genreDistribution = books.flatMap { it.subjects?.split(",") ?: emptyList() }
                .groupingBy { it }
                .eachCount()
                .map { (genre, count) -> Genre(name = genre, count = count) }
                .sortedByDescending { it.count },


            readingActivities = sortedActivities


        )
    }

    private fun calculateReadingStreaks(
        sortedActivities: List<ReadingActivity>,
        currentDate: LocalDate
    ): Pair<Int, Int> {
        var currentStreak = 0
        var longestStreak = 0
        var lastReadDate: LocalDate? = null

        for (activity in sortedActivities) {
            // Only consider activities that are at least 1 minute long
            if (activity.readingTime >= 60000) {
                val activityDate = Instant.ofEpochMilli(activity.date).atZone(ZoneId.systemDefault()).toLocalDate()

                if (lastReadDate == null || activityDate == lastReadDate.plusDays(1)) {
                    currentStreak++
                    if (currentStreak > longestStreak) {
                        longestStreak = currentStreak
                    }
                } else if (activityDate != lastReadDate) {
                    currentStreak = 1
                }

                lastReadDate = activityDate
            }
        }

        // Check if the streak is still active
        if (lastReadDate != null) {
            val daysSinceLastRead = ChronoUnit.DAYS.between(lastReadDate, currentDate)
            if (daysSinceLastRead > 1) {
                currentStreak = 0
            }
        } else {
            // No valid reading activities found
            currentStreak = 0
        }

        return Pair(longestStreak, currentStreak)
    }
}








