package com.capstone.skinpal.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.capstone.skinpal.data.local.entity.AnalysisEntity

@Dao
interface SkinAnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity)
}