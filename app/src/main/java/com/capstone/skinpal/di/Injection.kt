package com.capstone.skinpal.di

import android.content.Context
import com.capstone.skinpal.data.local.room.ArticleDatabase
import com.capstone.skinpal.data.remote.retrofit.ApiConfig
import com.capstone.skinpal.ui.Repository

object Injection {
    fun provideEventRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val database = ArticleDatabase.getInstance(context)
        val dao = database.articleDao()
        return Repository.getInstance(apiService, dao)
    }
}