package com.example.uread.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "shelf_book",
    primaryKeys = ["shelfName", "bookUri"],
    foreignKeys = [
        ForeignKey(
            entity = Shelf::class,
            parentColumns = ["name"],
            childColumns = ["shelfName"]
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["uri"],
            childColumns = ["bookUri"]
        )
    ]
)
data class BookShelf(
    val shelfName: String,
    val bookUri: String
)

