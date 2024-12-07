package com.capstone.skinpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skin_analysis")
data class AnalysisEntity(
    val userId: String,
    val week: String,
    val skinType: String,
    val acne : String,
    val redness : String,
    val wrinkles: String,
    val recommendations: String,  // Store as JSON string
    @PrimaryKey()
    val timestamp: Long = System.currentTimeMillis()
)