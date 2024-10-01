package com.ricdev.uread.domain.use_case.books

import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class DeleteBookByUriUseCase @Inject constructor(private val repository: BooksRepository)  {
    suspend operator fun invoke(bookUri: String) {
        repository.deleteBookByUri(bookUri)
    }
}