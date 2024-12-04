package com.capstone.skinpal.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var user: String? = null,
    var token: String? = null,
    var isLogin: Boolean = false
) : Parcelable