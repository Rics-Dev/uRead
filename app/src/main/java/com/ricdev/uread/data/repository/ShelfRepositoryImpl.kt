package com.ricdev.uread.data.repository

import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookShelf
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.data.source.local.dao.BookDao
import com.ricdev.uread.data.source.local.dao.BookShelfDao
import com.ricdev.uread.data.source.local.dao.ShelfDao
import com.ricdev.uread.domain.repository.ShelfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShelfRepositoryImpl @Inject constructor(
    private val shelfDao: ShelfDao,
    private val bookShelfDao: BookShelfDao,
    private val bookDao: BookDao
) : ShelfRepository {

    override fun getShelves(): Flow<List<Shelf>> = shelfDao.getAllShelves().flowOn(Dispatchers.IO)

    override suspend fun getShelfById(shelfId: Long): Shelf? = withContext(Dispatchers.IO) {
        shelfDao.getShelfById(shelfId)
    }

    override suspend fun addShelf(shelf: Shelf): Long = withContext(Dispatchers.IO) {
        shelfDao.insert(shelf)
    }

    override suspend fun updateShelf(shelf: Shelf) = withContext(Dispatchers.IO) {
        shelfDao.update(shelf)
    }

    override suspend fun deleteShelf(shelf: Shelf) = withContext(Dispatchers.IO) {
        shelfDao.delete(shelf)
    }

    override suspend fun addBookToShelf(bookId: Long, shelfId: Long) = withContext(Dispatchers.IO) {
        bookShelfDao.insert(BookShelf(bookId, shelfId))
    }

    override suspend fun removeBookFromShelf(bookId: Long, shelfId: Long) = withContext(Dispatchers.IO) {
        bookShelfDao.delete(BookShelf(bookId, shelfId))
    }

    override fun getBooksForShelf(shelfId: Long): Flow<List<Book>> = flow {
        val bookIds = bookShelfDao.getBooksForShelf(shelfId).map { it.bookId }
        emit(bookDao.getBooksByIds(bookIds))
    }.flowOn(Dispatchers.IO)

    override fun getShelvesForBook(bookId: Long): Flow<List<Shelf>> = flow {
        val shelfIds = bookShelfDao.getShelvesForBook(bookId).map { it.shelfId }
        emit(shelfDao.getShelfsByIds(shelfIds))
    }.flowOn(Dispatchers.IO)
}