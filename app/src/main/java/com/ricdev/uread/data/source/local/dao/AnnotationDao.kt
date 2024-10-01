package com.ricdev.uread.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ricdev.uread.data.model.BookAnnotation
import kotlinx.coroutines.flow.Flow


@Dao
interface AnnotationDao {
    @Query("SELECT * FROM annotations")
    fun getAllAnnotations(): Flow<List<BookAnnotation>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(annotation: BookAnnotation): Long

    @Update
    suspend fun update(annotation: BookAnnotation)

    @Delete
    suspend fun delete(annotation: BookAnnotation)

    @Query("SELECT * FROM annotations WHERE bookId = :bookId")
    fun getAnnotationsForBook(bookId: Long): Flow<List<BookAnnotation>>
}