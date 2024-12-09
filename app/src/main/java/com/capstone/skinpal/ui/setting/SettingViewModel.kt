package com.capstone.skinpal.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun getNotificationSetting(): LiveData<Boolean> = pref.getNotificationSetting().asLiveData()

    fun saveNotificationSetting(isNotificationEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotificationEnabled)
        }
    }
}