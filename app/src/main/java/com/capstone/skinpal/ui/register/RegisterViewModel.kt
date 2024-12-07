package com.capstone.skinpal.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.ui.Repository

class RegisterViewModel(private val repository: Repository): ViewModel() {

    private val _registrationResult = MutableLiveData<Result<String>>()
    val registrationResult: LiveData<Result<String>> = _registrationResult

    fun register(name: String, user_id: String, email: String, password: String) {
        repository.register(name, user_id, email, password).observeForever { result ->
            _registrationResult.value = result!!
        }
    }
}