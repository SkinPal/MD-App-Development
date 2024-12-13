package com.capstone.skinpal.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.capstone.skinpal.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 5, exportSchema = false)
@TypeConverters(com.capstone.skinpal.ui.product.Converters::class)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao() : ProductDao

    companion object {
        @Volatile
        private var instance: ProductDatabase? = null

        fun getInstance(context: Context): ProductDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java, "Product.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
