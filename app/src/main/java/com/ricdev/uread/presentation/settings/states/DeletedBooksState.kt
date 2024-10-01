package com.ricdev.uread.presentation.settings.states

import com.ricdev.uread.data.model.Book

sealed class DeletedBooksState {
    data object Loading : DeletedBooksState()
    data class Error(val message: String) : DeletedBooksState()
    data class Success(val books: List<Book>) : DeletedBooksState()
}