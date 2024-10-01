package com.ricdev.uread.domain.model
import com.ricdev.uread.data.model.Book

data class Author(
    val name: String,
    val books: List<Book>
)
