package com.example.uread.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.uread.data.model.Book


@Database(entities = [Book::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}