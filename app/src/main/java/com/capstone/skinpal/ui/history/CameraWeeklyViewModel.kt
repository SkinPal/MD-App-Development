package com.capstone.skinpal.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.ui.Repository
import java.io.File

class CameraWeeklyViewModel(private val repository: Repository) : ViewModel(){

    fun uploadImage(
        imageFile: File,
        user_id: String,
        week: String
    ) = repository.uploadImage(
        imageFile = imageFile,
        user_id = user_id,
        week = week
    )

    fun analyzeImage(
        imageFile: File,
        user_id: String,
        week: String
    ) = repository.analyzeImage(
        imageFile = imageFile,
        user_id = user_id,
        week = week
    )

    fun getImage(userId: String, week: String) = repository.getImage(userId, week)

    fun getResult(): LiveData<Result<AnalysisEntity>> {
        return repository.getResult()
    }
}