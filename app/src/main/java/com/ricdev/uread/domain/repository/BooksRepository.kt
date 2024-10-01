package com.ricdev.uread.domain.repository

import androidx.paging.PagingSource
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.SortOption
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    fun getAllBooks(): Flow<List<Book>>

    fun getAllBooks(
        sortOption: SortOption,
        isAscending: Boolean,
        readingStatuses: Set<ReadingStatus>,
        fileTypes: Set<FileType>
    ): PagingSource<Int, Book>

    fun getDeletedBooks(): Flow<List<Book>>
    suspend fun getAllBookUris(): List<String>
    suspend fun getBookById(bookId: Long): Book?
    suspend fun insertBook(book: Book)
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(book: Book)
    suspend fun deleteBookByUri(bookUri: String)

    suspend fun getReadingProgress(bookId: Long): String
    suspend fun setReadingProgress(bookId: Long, locator: String, progression: Float)
    suspend fun setReadingStatus(bookId: Long, status: ReadingStatus)






    // annotation (Highlights / Underlines)
    suspend fun getAllAnnotations(): Flow<List<BookAnnotation>>
    suspend fun getAnnotations(bookId: Long): Flow<List<BookAnnotation>>
    suspend fun addAnnotation(annotation: BookAnnotation): Long
    suspend fun updateAnnotation(annotation: BookAnnotation)
    suspend fun deleteAnnotation(annotation: BookAnnotation)



    // Notes
    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun getNotesForBook(bookId: Long): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)


    // Bookmarks
    suspend fun getAllBookmarks(): Flow<List<Bookmark>>
    suspend fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>>
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun updateBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark)





    // Reading Activity
    suspend fun insertOrUpdateReadingActivity(readingActivity: ReadingActivity)
    suspend fun getReadingActivityByDate(date: Long): ReadingActivity?
    suspend fun getTotalReadingTime(): Long?
    suspend fun getAllReadingActivities(): Flow<List<ReadingActivity>>
}