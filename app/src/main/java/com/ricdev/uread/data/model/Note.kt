package com.ricdev.uread.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bookId"])]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locator: String,
    val selectedText: String,
    val note: String,
    val color: String,
    val bookId: Long
)