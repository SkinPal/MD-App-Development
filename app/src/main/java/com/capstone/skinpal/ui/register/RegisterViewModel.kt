package com.capstone.skinpal.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.ui.Repository

class RegisterViewModel(private val repository: Repository): ViewModel() {

    private val _registrationResult = MutableLiveData<Result<String>>()
    val registrationResult: LiveData<Result<String>> = _registrationResult

    fun register(user_id: String, name: String, email: String, password: String) {
        repository.register(user_id, name, email, password).observeForever { result ->
            _registrationResult.value = result!!
        }
    }
}