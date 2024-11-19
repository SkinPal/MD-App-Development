package com.capstone.skinpal.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.skinpal.data.local.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 3, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao() : ArticleDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null

        fun getInstance(context: Context): ArticleDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java, "Article.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
