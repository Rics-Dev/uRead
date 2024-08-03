package com.example.uread.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class BookWithShelves(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "uri",
        entityColumn = "name",
        associateBy = Junction(BookShelf::class)
    )
    val shelves: List<Shelf>
)
