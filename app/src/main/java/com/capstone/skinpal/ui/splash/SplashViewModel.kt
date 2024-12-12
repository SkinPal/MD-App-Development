package com.capstone.skinpal.ui.splash

import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.ui.Repository

class SplashViewModel(private val repository: Repository) : ViewModel() {

    fun getUserSession(): UserModel {
        return repository.getUserSession()
    }
}
