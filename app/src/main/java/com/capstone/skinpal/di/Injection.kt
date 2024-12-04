package com.capstone.skinpal.di

import android.content.Context
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.room.ArticleDatabase
import com.capstone.skinpal.data.local.room.ImageDatabase
import com.capstone.skinpal.data.local.room.ProductDatabase
import com.capstone.skinpal.data.remote.retrofit.ApiConfig
import com.capstone.skinpal.ui.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val database = ArticleDatabase.getInstance(context)
        val imageDatabase = ImageDatabase.getInstance(context)
        val productDatabase = ProductDatabase.getInstance(context)
        val articleDao = database.articleDao()
        val imageDao = imageDatabase.imageDao()
        val productDao = productDatabase.productDao()
        val pref = UserPreference(context)
        val user = pref.getSession()
        val apiService = ApiConfig.getApiService(user.token!!)
        return Repository.getInstance(apiService, articleDao, productDao, pref, imageDao)
    }
}