@file:Suppress("unused", "RedundantSuppression")

package com.capstone.skinpal.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.skinpal.ui.setting.SettingPreferences
import kotlin.also
import kotlin.jvm.java

class ViewModelFactory(
    private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(com.capstone.skinpal.ui.product.ProductViewModel::class.java) -> {
                com.capstone.skinpal.ui.product.ProductViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.home.HomeViewModel::class.java) -> {
                com.capstone.skinpal.ui.home.HomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.login.LoginViewModel::class.java) -> {
                com.capstone.skinpal.ui.login.LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.register.RegisterViewModel::class.java) -> {
                com.capstone.skinpal.ui.register.RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.history.HistoryViewModel::class.java) -> {
                com.capstone.skinpal.ui.history.HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.camera.CameraViewModel::class.java) -> {
                com.capstone.skinpal.ui.camera.CameraViewModel(repository) as T
            }

            modelClass.isAssignableFrom(com.capstone.skinpal.ui.history.CameraWeeklyViewModel::class.java) -> {
                com.capstone.skinpal.ui.history.CameraWeeklyViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.capstone.skinpal.ui.splash.SplashViewModel::class.java) -> {
                com.capstone.skinpal.ui.splash.SplashViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.capstone.skinpal.ui.setting.AccountViewModel::class.java) -> {
                com.capstone.skinpal.ui.setting.AccountViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
