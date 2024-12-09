package com.capstone.skinpal.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun getThemeSetting(): LiveData<Boolean> {
        viewModelScope
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    private val _isPeriodicTaskEnabled = MutableLiveData<Boolean>()
    val isPeriodicTaskEnabled: LiveData<Boolean> get() = _isPeriodicTaskEnabled

    fun getNotificationSetting(): LiveData<Boolean> {
        viewModelScope
        return pref.getNotificationSetting().asLiveData()
    }

    fun saveNotificationSetting(isNotificationActive: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotificationActive)
        }
    }
}