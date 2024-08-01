package com.example.uread.books.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Query("DELETE FROM books WHERE uri = :uri")
    fun deleteBookByUri(uri: String)

    @Query("SELECT * FROM books WHERE uri = :uri")
    fun getBookByUri(uri: String): Book?

}