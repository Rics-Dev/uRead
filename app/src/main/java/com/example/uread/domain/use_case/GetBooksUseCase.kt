package com.example.uread.domain.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.uread.data.model.Book
import com.example.uread.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetBooksUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    operator fun invoke(): Flow<PagingData<Book>> = Pager(
        config = PagingConfig(
            pageSize = 9,
            enablePlaceholders = true,
        )
    ) {
        repository.getAllBooks()
    }.flow
}



//class GetBooksUseCase @Inject constructor(private val repository: BooksRepository) {
//    operator fun invoke(): PagingSource<Int, Book> {
//        return repository.getAllBooks()
//    }
//}