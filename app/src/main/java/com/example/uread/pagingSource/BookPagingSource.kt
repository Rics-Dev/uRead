package com.example.uread.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.uread.data.model.Book
import com.example.uread.data.source.local.BookDao

class BookPagingSource(
    private val bookDao: BookDao
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val books = bookDao.getPagedBooks(page * pageSize, pageSize)
            val nextKey = if (books.isEmpty()) null else page + 1
            val prevKey = if (page > 0) page - 1 else null

            LoadResult.Page(
                data = books,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}