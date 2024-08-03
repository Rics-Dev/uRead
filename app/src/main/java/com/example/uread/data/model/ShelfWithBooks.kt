package com.example.uread.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ShelfWithBooks(
    @Embedded val shelf: Shelf,
    @Relation(
        parentColumn = "name",
        entityColumn = "uri",
        associateBy = Junction(BookShelf::class)
    )
    val books: List<Book>
)