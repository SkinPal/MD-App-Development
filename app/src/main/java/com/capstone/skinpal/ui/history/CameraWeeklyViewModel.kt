package com.capstone.skinpal.ui.history

import androidx.lifecycle.ViewModel
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

    fun getImage(week: Int) = repository.getImage(week)
}