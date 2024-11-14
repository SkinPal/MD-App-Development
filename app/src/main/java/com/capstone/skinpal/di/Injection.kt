package com.capstone.skinpal.di

import android.content.Context
import com.capstone.skinpal.ui.Repository

object Injection {
    fun provideEventRepository(context: Context): Repository {
       /* val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return Repository.getInstance(apiService, dao) */
        return Repository.getInstance()
    }
}