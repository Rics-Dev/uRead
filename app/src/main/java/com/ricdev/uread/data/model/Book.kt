package com.ricdev.uread.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val fileType: FileType,
    val title: String,
    val authors: String,
    val description: String?,
    val publishDate: String?, // New: Publication date
    val publisher: String?, // New: Publisher
    val language: String?, // New: Primary language
    val numberOfPages: Int?, // New: Total number of pages
    val subjects: String?, // New: Categories or genres
    val coverPath: String?,
    val locator: String,
    val progression: Float = 0f, // reading progression in %
    val lastOpened: Long? = null, // timestamp of the last time the book was opened
    val deleted: Boolean = false, // flag to mark the book as deleted
    val rating: Float = 0f, // rating of the book
    val isFavorite: Boolean = false, // flag to mark the book as favorite
    val readingStatus: ReadingStatus? = ReadingStatus.NOT_STARTED, // reading status of the book
    val readingTime: Long = 0, // total time spent reading the book in milliseconds
    val startReadingDate: Long? = null, // timestamp of when the user started reading the book
    val endReadingDate: Long? = null, // timestamp of when the user finished reading the book
    val review: String? = null,
    val duration: Long? = null, // Total duration of the audiobook in milliseconds
    val narrator: String? = null, // Name of the audiobook narrator
)

enum class FileType {
    EPUB,
    PDF,
    AUDIOBOOK
}

enum class ReadingStatus {
    NOT_STARTED,
    IN_PROGRESS,
    FINISHED
}