package com.ricdev.uread.data.source.local.dao

import androidx.room.*
import com.ricdev.uread.data.model.BookShelf

@Dao
interface BookShelfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookShelf: BookShelf)

    @Delete
    suspend fun delete(bookShelf: BookShelf)

    @Query("SELECT * FROM book_shelf WHERE bookId = :bookId")
    suspend fun getShelvesForBook(bookId: Long): List<BookShelf>

    @Query("SELECT * FROM book_shelf WHERE shelfId = :shelfId")
    suspend fun getBooksForShelf(shelfId: Long): List<BookShelf>
}