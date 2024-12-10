package com.capstone.skinpal.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.capstone.skinpal.data.local.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getProduct(): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: List<ProductEntity>)

    @Query("DELETE FROM product")
    suspend fun deleteAll()

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("SELECT * FROM product WHERE name LIKE :query")
    fun searchProducts(query: String): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE name = :name")
    fun getProductsByName(name: String): LiveData<ProductEntity>

    @Query("SELECT * FROM product where bookmarked = 1")
    fun getBookmarkedProduct(): LiveData<List<ProductEntity>>

    @Query("SELECT EXISTS(SELECT * FROM product WHERE name = :name AND bookmarked = 1)")
    suspend fun isProductBookmarked(name: String): Boolean

}