package com.capstone.skinpal.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinAnalysisDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnalysis(analysis: AnalysisEntity)

    @Query("SELECT * FROM skin_analysis WHERE userId = :userId")
    fun getAnalysisByUserId(userId: String): AnalysisEntity

    @Query("SELECT * FROM skin_analysis WHERE userId = :userId AND week = :week")
    fun getAnalysisByUserIdAndWeek(userId: String, week: String): Flow<AnalysisEntity?>

    @Query("DELETE FROM skin_analysis WHERE userId = :userId")
    suspend fun deleteAnalysisByUserId(userId: String)
}