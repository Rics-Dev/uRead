package com.example.uread.domain.use_case

import com.example.uread.domain.repository.BooksRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    suspend operator fun invoke(uri: String) = repository.deleteBookByUri(uri)
}