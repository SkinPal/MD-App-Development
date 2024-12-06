package com.capstone.skinpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skin_analysis")
data class AnalysisEntity(
    @PrimaryKey()
    val userId: String,
    val week: String,
    val skinType: String,
    val skinConditions: String, // Store as JSON string
    val recommendations: String  // Store as JSON string
)