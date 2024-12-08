package com.capstone.skinpal.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.skinpal.data.local.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getProduct(): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: List<ProductEntity>)

    @Query("DELETE FROM product")
    suspend fun deleteAll()

    @Query("SELECT * FROM product WHERE name LIKE :query")
    fun searchProducts(query: String): LiveData<List<ProductEntity>>

}