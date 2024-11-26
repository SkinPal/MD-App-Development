@file:Suppress("unused", "RedundantSuppression")

package com.capstone.skinpal.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.capstone.skinpal.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM image")
    fun getItem(): Flow<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertItem(image: ImageEntity)

    @Query("SELECT * FROM image WHERE id = :id")
    fun getImageById(id: Int): ImageEntity

    @Update
    suspend fun updateItem(item: ImageEntity)

    @Query("DELETE FROM image WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int): Int

    @Query("SELECT * FROM image WHERE id = :itemId")
    fun getItemById(itemId: Int): LiveData<ImageEntity>
}