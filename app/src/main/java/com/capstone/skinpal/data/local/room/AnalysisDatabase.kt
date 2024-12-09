package com.capstone.skinpal.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.ui.product.Converters

@TypeConverters(Converters::class)
@Database(entities = [AnalysisEntity::class], version = 11, exportSchema = false)
abstract class AnalysisDatabase : RoomDatabase() {


    abstract fun skinAnalysisDao() : SkinAnalysisDao

    companion object {
        @Volatile
        private var instance: AnalysisDatabase? = null

        fun getInstance(context: Context): AnalysisDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AnalysisDatabase::class.java, "Analysis.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
