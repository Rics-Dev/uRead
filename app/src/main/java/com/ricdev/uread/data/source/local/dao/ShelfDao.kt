package com.ricdev.uread.data.source.local.dao

import androidx.room.*
import com.ricdev.uread.data.model.Shelf
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shelf: Shelf): Long

    @Update
    suspend fun update(shelf: Shelf)

    @Delete
    suspend fun delete(shelf: Shelf)

    @Query("SELECT * FROM shelves ORDER BY `order` ASC")
    fun getAllShelves(): Flow<List<Shelf>>

    @Query("SELECT * FROM shelves WHERE id = :shelfId")
    suspend fun getShelfById(shelfId: Long): Shelf?

    @Query("SELECT * FROM shelves WHERE id IN (:shelfIds)")
    suspend fun getShelfsByIds(shelfIds: List<Long>): List<Shelf>
}