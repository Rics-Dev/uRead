package com.example.uread.domain.repository

import androidx.paging.PagingSource
import com.example.uread.data.model.Book
import org.readium.r2.shared.publication.Locator

interface BooksRepository {
    fun getAllBooks(): PagingSource<Int, Book>
    suspend fun getAllBookUris(): List<String>
    suspend fun insertBook(book: Book)
    suspend fun insertBooks(books: List<Book>)
    suspend fun deleteBookByUri(uri: String)

    suspend fun getReadingProgress(uri: String): String
    suspend fun setReadingProgress(uri: String, locator: String)
}