package com.capstone.skinpal.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class CameraWeeklyViewModel(private val repository: Repository) : ViewModel(){

    fun getImage(week: Int)= repository.getImage(week)

    fun saveItem(item: ImageEntity) {
        viewModelScope.launch {
            repository.saveImage(item)
        }
    }
}