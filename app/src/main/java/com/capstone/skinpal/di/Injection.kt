package com.capstone.skinpal.di

import android.content.Context
import com.capstone.skinpal.data.local.room.ArticleDatabase
import com.capstone.skinpal.data.local.room.ImageDatabase
import com.capstone.skinpal.data.remote.retrofit.ApiConfig
import com.capstone.skinpal.ui.Repository

object Injection {
    fun provideEventRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val database = ArticleDatabase.getInstance(context)
        val imageDatabase = ImageDatabase.getInstance(context)
        val articleDao = database.articleDao()
        val imageDao = imageDatabase.imageDao()
        return Repository.getInstance(apiService, articleDao, imageDao)
    }
}