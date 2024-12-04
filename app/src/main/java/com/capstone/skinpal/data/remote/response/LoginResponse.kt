package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("token_type")
	val tokenType: String,

	@field:SerializedName("user")
	val user: User
)

data class User(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("email")
	val email: String
)
