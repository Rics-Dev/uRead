package com.example.uread.books.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Book::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}