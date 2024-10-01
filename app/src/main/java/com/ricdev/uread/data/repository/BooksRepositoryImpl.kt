package com.ricdev.uread.data.repository

import androidx.paging.PagingSource
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.SortOption
import com.ricdev.uread.data.source.local.dao.AnnotationDao
import com.ricdev.uread.data.source.local.dao.BookDao
import com.ricdev.uread.data.source.local.dao.BookmarkDao
import com.ricdev.uread.data.source.local.dao.NoteDao
import com.ricdev.uread.data.source.local.dao.ReadingActivityDao
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val annotationDao: AnnotationDao,
    private val noteDao: NoteDao,
    private val bookmarkDao: BookmarkDao,
    private val readingActivityDao: ReadingActivityDao,
) : BooksRepository {
    override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    override fun getAllBooks(
        sortOption: SortOption,
        isAscending: Boolean,
        readingStatuses: Set<ReadingStatus>,
        fileTypes: Set<FileType>,
    ): PagingSource<Int, Book> {
        return bookDao.getAllBooksSorted(
            sortOption.name.lowercase(),
            isAscending,
            readingStatuses = readingStatuses.toList().takeIf { it.isNotEmpty() },
            fileTypes = fileTypes.toList().takeIf { it.isNotEmpty() }
        )
    }

    override fun getDeletedBooks(): Flow<List<Book>> {
        return bookDao.getDeletedBooks()
    }



    override suspend fun getAllBookUris(): List<String> = withContext(Dispatchers.IO) {
        bookDao.getAllBookUris()
    }

    override suspend fun getBookById(bookId: Long): Book? = withContext(Dispatchers.IO) {
        bookDao.getBookById(bookId)
    }

    override suspend fun insertBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.insertBook(book)
    }

    override suspend fun updateBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.update(book)
    }

    override suspend fun deleteBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.delete(book)
    }

    override suspend fun deleteBookByUri(bookUri: String) = withContext(Dispatchers.IO) {
        bookDao.deleteBookByUri(bookUri)
    }


    override suspend fun getReadingProgress(bookId: Long): String = withContext(Dispatchers.IO) {
        bookDao.getReadingProgress(bookId)
    }

    override suspend fun setReadingStatus(bookId: Long, status: ReadingStatus) {
        bookDao.setReadingStatus(bookId, status)
    }

    override suspend fun setReadingProgress(bookId: Long, locator: String, progression: Float) = withContext(Dispatchers.IO) {
        bookDao.setReadingProgress(bookId, locator, progression)
    }


    override suspend fun getAllAnnotations(): Flow<List<BookAnnotation>> = withContext(Dispatchers.IO) {
        annotationDao.getAllAnnotations()
    }

    override suspend fun getAnnotations(bookId: Long): Flow<List<BookAnnotation>> = withContext(Dispatchers.IO)  {
        annotationDao.getAnnotationsForBook(bookId)
    }
    override suspend fun addAnnotation(annotation: BookAnnotation): Long {
        return annotationDao.insert(annotation)
    }
    override suspend fun updateAnnotation(annotation: BookAnnotation) {
        annotationDao.update(annotation)
    }
    override suspend fun deleteAnnotation(annotation: BookAnnotation) {
        annotationDao.delete(annotation)
    }


    override suspend fun getAllNotes(): Flow<List<Note>> = withContext(Dispatchers.IO) {
        noteDao.getAllNotes()
    }

    override suspend fun getNotesForBook(bookId: Long): Flow<List<Note>> = withContext(Dispatchers.IO) {
        noteDao.getNotesForBook(bookId)
    }

    override suspend fun addNote(note: Note) {
        noteDao.insert(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }




    override suspend fun getAllBookmarks(): Flow<List<Bookmark>> = withContext(Dispatchers.IO) {
        bookmarkDao.getAllBookmarks()
    }

    override suspend fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>> = withContext(Dispatchers.IO) {
        bookmarkDao.getBookmarksForBook(bookId)
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(bookmark)
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.update(bookmark)
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark)
    }


    // Reading Activity
    override suspend fun insertOrUpdateReadingActivity(readingActivity: ReadingActivity) {
        readingActivityDao.insertOrUpdate(readingActivity)
    }

    override suspend fun getReadingActivityByDate(date: Long): ReadingActivity? {
        return readingActivityDao.getReadingActivityByDate(date)
    }

    override suspend fun getTotalReadingTime(): Long? {
        return readingActivityDao.getTotalReadingTime()
    }


    override suspend fun getAllReadingActivities(): Flow<List<ReadingActivity>> {
        return readingActivityDao.getAllReadingActivities()
    }


}