package com.capstone.skinpal.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.skinpal.data.local.entity.ImageEntity
import kotlin.also

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var instance: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java, "Image.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
