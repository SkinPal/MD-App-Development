package com.capstone.skinpal.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.remote.response.ProfileResponse
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AccountViewModel(
    private val repository: Repository
): ViewModel() {

    private val _userProfile = MutableLiveData<ProfileResponse?>()
    val userProfile: MutableLiveData<ProfileResponse?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadResponse = MutableLiveData<String?>()
    val uploadResponse: LiveData<String?> = _uploadResponse

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

    fun uploadProfile(imagePart: MultipartBody.Part) {
        val userId = repository.getUserSession().user // Ambil userId dari sesi
        if (userId.isNullOrEmpty()) {
            _error.postValue("User ID tidak ditemukan")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.uploadProfileImage(imagePart, userId)
                _uploadResponse.postValue(response?.publicUrl)
            } catch (e: Exception) {
                Log.e("ViewModel", "Upload Failed", e)
                _error.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
    }

}