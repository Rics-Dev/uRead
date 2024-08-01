package com.example.uread.domain.repository

import androidx.paging.PagingSource
import com.example.uread.data.model.Book

interface BooksRepository {
    fun getAllBooks(): PagingSource<Int, Book>
    suspend fun getAllBookUris(): List<String>
    suspend fun insertBook(book: Book)
    suspend fun deleteBookByUri(uri: String)
}