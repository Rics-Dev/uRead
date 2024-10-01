package com.ricdev.uread.domain.use_case.books

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.FileType
import com.ricdev.uread.data.model.ReadingStatus
import com.ricdev.uread.data.model.SortOption
import com.ricdev.uread.domain.repository.BooksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: BooksRepository
) {
    operator fun invoke(
        sortOption: SortOption,
        isAscending: Boolean,
        readingStatuses: Set<ReadingStatus>,
        fileTypes: Set<FileType>
    ): Flow<PagingData<Book>> = Pager(
        config = PagingConfig(
            pageSize = 9,
            enablePlaceholders = true,
        )
    ) {
        repository.getAllBooks(sortOption, isAscending, readingStatuses, fileTypes)
    }.flow
}