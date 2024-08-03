package com.example.uread.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shelf")
data class Shelf(
    @PrimaryKey val name: String,
)
