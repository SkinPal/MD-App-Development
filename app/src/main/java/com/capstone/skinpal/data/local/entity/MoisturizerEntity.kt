package com.capstone.skinpal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moisturizer")
data class MoisturizerEntity(
   val userId: String,
   val week: String,
   val analysisId: String, // Identifier for the analysis
   val name: String,
   val description: String
)
