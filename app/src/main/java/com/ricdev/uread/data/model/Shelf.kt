package com.ricdev.uread.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelves")
data class Shelf(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val order: Int
)
