package com.capstone.skinpal.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class CameraViewModel(private val repository: Repository) : ViewModel(){
    /*fun saveItem(item: ImageEntity) {
        viewModelScope.launch {
            repository.uploadImage(item)
        }
    }*/
}