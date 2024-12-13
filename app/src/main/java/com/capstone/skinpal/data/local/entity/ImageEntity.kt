package com.capstone.skinpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image: String,
    val week: Int,
)