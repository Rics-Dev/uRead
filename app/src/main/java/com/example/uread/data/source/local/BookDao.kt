package com.example.uread.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.uread.data.model.Book
import com.example.uread.data.model.BookShelf
import com.example.uread.data.model.BookWithShelves
import com.example.uread.data.model.Shelf
import com.example.uread.data.model.ShelfWithBooks

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): PagingSource<Int, Book>



    @Query("SELECT uri FROM books")
    suspend fun getAllBookUris(): List<String>




    @Query("SELECT * FROM books WHERE uri = :uri")
    fun getBookByUri(uri: String): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<Book>)

    @Query("DELETE FROM books WHERE uri = :uri")
    fun deleteBookByUri(uri: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(books: Book)


    @Query("SELECT locator FROM books WHERE uri = :uri")
    fun getReadingProgress(uri: String): String

    @Query("UPDATE books SET locator = :locator WHERE uri = :uri")
    fun setReadingProgress(uri: String, locator: String)
//
//
//    @Transaction
//    @Query("SELECT * FROM shelf")
//    fun getShelvesWithBooks(): List<ShelfWithBooks>
//
//    @Transaction
//    @Query("SELECT * FROM books")
//    fun getBooksWithShelves(): List<BookWithShelves>


//    @Delete
//    fun deleteBook(uri: String)


//
//    @Insert
//    fun insertShelf(shelf: Shelf)
//
//    @Insert
//    fun insertShelfBookCrossRef(crossRef: BookShelf)
//









}