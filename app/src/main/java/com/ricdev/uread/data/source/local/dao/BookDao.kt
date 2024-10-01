package com.ricdev.uread.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE deleted = 0")
    fun getAllBooks(): Flow<List<Book>>


    @Query(
        """
        SELECT * FROM books 
        WHERE deleted = 0 
        AND (:readingStatuses IS NULL OR readingStatus IN (:readingStatuses))
        AND (:fileTypes IS NULL OR fileType IN (:fileTypes))
        ORDER BY 
        CASE WHEN :sortBy = 'last_opened' AND :isAsc = 1 THEN lastOpened END ASC, 
        CASE WHEN :sortBy = 'last_opened' AND :isAsc = 0 THEN lastOpened END DESC, 
        CASE WHEN :sortBy = 'title' AND :isAsc = 1 THEN title END ASC, 
        CASE WHEN :sortBy = 'title' AND :isAsc = 0 THEN title END DESC, 
        CASE WHEN :sortBy = 'author' AND :isAsc = 1 THEN authors END ASC, 
        CASE WHEN :sortBy = 'author' AND :isAsc = 0 THEN authors END DESC,
        CASE WHEN :sortBy = 'rating' AND :isAsc = 1 THEN rating END ASC, 
        CASE WHEN :sortBy = 'rating' AND :isAsc = 0 THEN rating END DESC, 
        CASE WHEN :sortBy = 'progression' AND :isAsc = 1 THEN progression END ASC, 
        CASE WHEN :sortBy = 'progression' AND :isAsc = 0 THEN progression END DESC
        """
    )
    fun getAllBooksSorted(
        sortBy: String,
        isAsc: Boolean,
        readingStatuses: List<ReadingStatus>?,
        fileTypes: List<FileType>?
    ): PagingSource<Int, Book>


    @Query("SELECT * FROM books WHERE deleted = 1")
    fun getDeletedBooks(): Flow<List<Book>>


    @Query("SELECT uri FROM books")
    suspend fun getAllBookUris(): List<String>


    @Query("SELECT * FROM books WHERE uri = :uri")
    fun getBookByUri(uri: String): Book?

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Long): Book?

    @Query("SELECT * FROM books WHERE id IN (:bookIds)")
    suspend fun getBooksByIds(bookIds: List<Long>): List<Book>




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(books: Book)


    @Transaction
    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)


    @Query("DELETE FROM books WHERE uri = :bookUri")
    fun deleteBookByUri(bookUri: String)




    @Query("SELECT locator FROM books WHERE id = :bookId")
    fun getReadingProgress(bookId: Long): String

    @Query("UPDATE books SET locator = :locator, progression = :progression WHERE id = :bookId")
    fun setReadingProgress(bookId: Long, locator: String, progression: Float)


    @Query("UPDATE books SET readingStatus = :status WHERE id = :bookId")
    suspend fun setReadingStatus(bookId: Long, status: ReadingStatus)






}

