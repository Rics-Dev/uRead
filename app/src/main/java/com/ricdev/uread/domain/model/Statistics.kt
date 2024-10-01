package com.ricdev.uread.domain.model

import com.ricdev.uread.data.model.ReadingActivity


data class Statistics(
    val totalBooks: Int = 0,
    val booksRead: Int = 0,
    val booksReadThisYear: Int = 0,
    val booksReadThisMonth: Int = 0,
    val booksInProgress: Int = 0,
    val booksToRead: Int = 0,
    val totalReadingTime: Long = 0,
    val averageDailyReadingTime: Long = 0,
    val averageReadingTimePerBook: Long = 0,
    val longestReadingStreak: Int = 0,
    val currentReadingStreak: Int = 0,
    val favoriteBooks: Int = 0,
    val ratedBooks: Int = 0,
    val averageRating: Double = 0.0,


    val totalNotes: Int = 0,
    val totalHighlights: Int = 0,
    val totalUnderlines: Int = 0,


    val favoriteAuthors: List<Author> = emptyList(),

    val genreDistribution: List<Genre> = emptyList(),


    val readingActivities: List<ReadingActivity> = emptyList()
)
