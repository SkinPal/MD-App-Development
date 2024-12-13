package com.capstone.skinpal.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.ui.Repository
import java.io.File

class CameraViewModel(private val repository: Repository) : ViewModel(){

    fun analyzeImage(
        imageFile: File,
        user_id: String,
        week: String
    ) = repository.analyzeImage(
        imageFile = imageFile,
        user_id = user_id,
        week = week
    )

    fun getImage(user_id: String, week: String) = repository.getImage(week, user_id)

    fun getResult(): LiveData<Result<AnalysisEntity>> {
        return repository.getResult()
    }
}