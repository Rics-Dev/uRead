package com.example.uread.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uread.data.model.Book

@Database(entities = [Book::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

//    companion object {
//        // Define your migration from version 1 to version 2
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                // Step 3: Drop the old table
//                db.execSQL("DROP TABLE books")
//
//                db.execSQL("""
//                    CREATE TABLE books (
//                        uri TEXT NOT NULL PRIMARY KEY,
//                        title TEXT NOT NULL,
//                        authors TEXT NOT NULL,
//                        description TEXT,
//                        coverPath TEXT,
//                        locator TEXT NOT NULL
//                    )
//                """.trimIndent())
//
//            }
//        }
//    }
}
