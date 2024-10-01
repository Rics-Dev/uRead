package com.ricdev.uread.domain.repository
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Shelf
import kotlinx.coroutines.flow.Flow

interface ShelfRepository {
    fun getShelves(): Flow<List<Shelf>>
    suspend fun getShelfById(shelfId: Long): Shelf?
    suspend fun addShelf(shelf: Shelf): Long
    suspend fun updateShelf(shelf: Shelf)
    suspend fun deleteShelf(shelf: Shelf)

    suspend fun addBookToShelf(bookId: Long, shelfId: Long)
    suspend fun removeBookFromShelf(bookId: Long, shelfId: Long)
    fun getBooksForShelf(shelfId: Long): Flow<List<Book>>
    fun getShelvesForBook(bookId: Long): Flow<List<Shelf>>
}