package com.capstone.skinpal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.capstone.skinpal.data.remote.response.SkinTypes
import com.capstone.skinpal.ui.product.Converters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "product")
@TypeConverters(Converters::class)
data class ProductEntity(
    @PrimaryKey
    val name: String,
    val imageUrl: String?,
    val description: String?,
    val ingredients: String?,
    val type: String?,
    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean?
)