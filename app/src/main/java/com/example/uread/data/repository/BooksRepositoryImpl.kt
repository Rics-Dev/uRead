package com.example.uread.data.repository

import androidx.paging.PagingSource
import com.example.uread.data.model.Book
import com.example.uread.data.source.local.BookDao
import com.example.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BooksRepository {
    override fun getAllBooks(): PagingSource<Int, Book> = bookDao.getAllBooks()

    override suspend fun getAllBookUris(): List<String> = withContext(Dispatchers.IO) {
        bookDao.getAllBookUris()
    }

    override suspend fun insertBook(book: Book) = withContext(Dispatchers.IO) {
        bookDao.insertBook(book)
    }

    override suspend fun insertBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        bookDao.insertBooks(books)
    }

    override suspend fun deleteBookByUri(uri: String) = withContext(Dispatchers.IO) {
        bookDao.deleteBookByUri(uri)
    }
}