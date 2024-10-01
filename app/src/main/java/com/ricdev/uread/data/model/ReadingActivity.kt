package com.ricdev.uread.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "reading_activities")
data class ReadingActivity(
    @PrimaryKey val date: Long, // Date in milliseconds
    val readingTime: Long, // Reading time in milliseconds
)
