package com.example.uread.domain.use_case

import com.example.uread.data.model.Book
import com.example.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertBookUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(book: Book) = withContext(Dispatchers.IO) {
        repository.insertBook(book)
    }
}