package com.example.uread.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uread.data.model.Book

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): PagingSource<Int, Book>


    @Query("SELECT uri FROM books")
    suspend fun getAllBookUris(): List<String>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Query("DELETE FROM books WHERE uri = :uri")
    fun deleteBookByUri(uri: String)

    @Query("SELECT * FROM books WHERE uri = :uri")
    fun getBookByUri(uri: String): Book?

}