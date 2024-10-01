package com.ricdev.uread.domain.use_case.notes

import com.ricdev.uread.data.model.Note
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesForBookUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(bookId: Long): Flow<List<Note>> {
        return repository.getNotesForBook(bookId)
    }
}