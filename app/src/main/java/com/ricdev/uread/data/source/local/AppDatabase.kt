package com.ricdev.uread.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.BookAnnotation
import com.ricdev.uread.data.model.BookShelf
import com.ricdev.uread.data.model.Bookmark
import com.ricdev.uread.data.model.Note
import com.ricdev.uread.data.model.ReadingActivity
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.data.source.local.dao.AnnotationDao
import com.ricdev.uread.data.source.local.dao.BookDao
import com.ricdev.uread.data.source.local.dao.BookShelfDao
import com.ricdev.uread.data.source.local.dao.BookmarkDao
import com.ricdev.uread.data.source.local.dao.NoteDao
import com.ricdev.uread.data.source.local.dao.ReadingActivityDao
import com.ricdev.uread.data.source.local.dao.ShelfDao

@Database(
    entities = [
        Book::class,
        BookAnnotation::class,
        Note::class,
        Bookmark::class,
        Shelf::class,
        BookShelf::class,
        ReadingActivity::class
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun noteDao(): NoteDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun shelfDao(): ShelfDao
    abstract fun bookShelfDao(): BookShelfDao
    abstract fun readingActivityDao(): ReadingActivityDao
}
