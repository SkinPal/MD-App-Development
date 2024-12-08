package com.capstone.skinpal.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinAnalysisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity)

    @Query("SELECT * FROM skin_analysis WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAnalysisByUserId(userId: String): AnalysisEntity?

    @Query("SELECT * FROM skin_analysis ORDER BY timestamp DESC LIMIT 1")
    fun getResult(): AnalysisEntity?

    @Query("SELECT * FROM skin_analysis WHERE userId = :userId AND week = :week")
    fun getAnalysisByUserIdAndWeek(userId: String, week: String): Flow<AnalysisEntity?>

    @Query("SELECT * FROM skin_analysis WHERE userId = :userId AND week = :week")
    fun getImageByUserIdAndWeek(userId: String, week: String): LiveData<AnalysisEntity?>

    @Query("DELETE FROM skin_analysis WHERE userId = :userId")
    suspend fun deleteAnalysisByUserId(userId: String)
}