package com.capstone.skinpal.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val repository: Repository) : ViewModel(){
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

    fun getImage(week: String) = repository.getImage(week)

    fun getResult(): LiveData<Result<AnalysisEntity>> {
        return repository.getResult()
    }
}