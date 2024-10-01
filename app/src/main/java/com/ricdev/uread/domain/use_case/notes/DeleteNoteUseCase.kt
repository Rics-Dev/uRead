package com.ricdev.uread.domain.use_case.notes

import com.ricdev.uread.data.model.Note
import com.ricdev.uread.domain.repository.BooksRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}