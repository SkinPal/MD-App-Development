package com.capstone.skinpal.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.remote.response.ProfileResponse
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository): ViewModel() {

    //fun getArticle() = repository.getArticle()
    private val _userProfile = MutableLiveData<ProfileResponse?>()
    val userProfile: MutableLiveData<ProfileResponse?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getAnalysisByUserId(userId: String): LiveData<Result<AnalysisEntity>> {
        return repository.getAnalysisByUserId(userId)
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                // Ambil userId dari sesi
                val userId = repository.getUserSession().user
                Log.d("DEBUG", "userId sebelum request: $userId") // Log userId

                if (userId!!.isEmpty()) {
                    _error.postValue("User ID kosong!")
                    return@launch
                }

                val response = repository.getUserProfile(userId)
                _userProfile.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to load profile: ${e.message}")
                Log.e("AccountViewModel", "Error fetching user profile", e)
            }
        }
    }
}