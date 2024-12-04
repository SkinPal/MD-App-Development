package com.capstone.skinpal.data.remote.retrofit

import com.google.gson.annotations.SerializedName

// RegisterRequest.kt
data class RegisterRequest(
    @SerializedName("nama") // Ubah ini
    val name: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password:String
)