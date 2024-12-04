package com.capstone.skinpal.data.remote.retrofit

import com.google.gson.annotations.SerializedName

// RegisterRequest.kt
data class LoginRequest(
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("password")
    val password:String
)