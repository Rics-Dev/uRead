package com.example.uread.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val uri: String,
    val title: String,
    val authors: String,
    val description: String?,
    val coverPath: String?,
    val locator: String
)