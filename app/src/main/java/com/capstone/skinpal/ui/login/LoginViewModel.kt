package com.capstone.skinpal.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.data.remote.response.LoginResponse
import com.capstone.skinpal.data.remote.retrofit.LoginRequest
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult.value = Result.Loading
                val loginRequest = LoginRequest(
                    user_id = userId,
                    password = password
                )
                val response = repository.login(loginRequest)
                _loginResult.value = Result.Success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}