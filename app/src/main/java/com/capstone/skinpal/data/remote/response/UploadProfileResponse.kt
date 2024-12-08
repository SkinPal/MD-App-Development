package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadProfileResponse(

	@field:SerializedName("public_url")
	val publicUrl: String,

	@field:SerializedName("message")
	val message: String
)
