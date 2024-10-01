package com.ricdev.uread.domain.use_case.books

import com.ricdev.uread.data.model.Book
import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(book: Book) {
        repository.updateBook(book)
    }

}