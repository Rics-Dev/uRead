package com.ricdev.uread.domain.use_case.books

import com.ricdev.uread.data.model.Book
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeletedBooksUseCase @Inject constructor(private val repository: BooksRepository) {
    operator fun invoke(): Flow<List<Book>> = repository.getDeletedBooks()
}