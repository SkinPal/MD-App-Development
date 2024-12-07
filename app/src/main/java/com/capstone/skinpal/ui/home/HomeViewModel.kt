package com.capstone.skinpal.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.ui.Repository

class HomeViewModel(private val repository: Repository): ViewModel() {

    //fun getArticle() = repository.getArticle()

    fun getAnalysisByUserId(userId: String): LiveData<Result<AnalysisEntity>> {
        return repository.getAnalysisByUserId(userId)
    }
}