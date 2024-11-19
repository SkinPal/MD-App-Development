package com.capstone.skinpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article")
data class ArticleEntity(

    @field:PrimaryKey
    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("urlToImage")
    val urlToImage: String,

    @field:SerializedName("url")
    val url: String? = null,

)
