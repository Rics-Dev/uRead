package com.example.uread.domain.use_case

import androidx.paging.PagingSource
import com.example.uread.data.model.Book
import com.example.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBookUrisUseCase @Inject constructor(private val repository: BooksRepository) {
    suspend operator fun invoke(): List<String> = withContext(Dispatchers.IO) {
        repository.getAllBookUris()
    }
}