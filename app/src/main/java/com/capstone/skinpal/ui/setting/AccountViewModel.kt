package com.capstone.skinpal.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: Repository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
    }
}