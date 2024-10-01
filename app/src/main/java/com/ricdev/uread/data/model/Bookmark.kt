package com.ricdev.uread.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bookId"])]
)
data class Bookmark (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val locator: String,
    val dateAndTime: Long,
    val color: String? = null,
)