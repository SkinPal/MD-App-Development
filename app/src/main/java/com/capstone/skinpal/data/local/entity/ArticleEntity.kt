package com.capstone.skinpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article")
data class ArticleEntity(

    @field:PrimaryKey
    val title: String,
    val urlToImage: String,
    val url: String? = null,

)
