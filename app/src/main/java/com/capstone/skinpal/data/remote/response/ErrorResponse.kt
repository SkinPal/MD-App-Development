package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("detail")
    val detail: String? = null
)