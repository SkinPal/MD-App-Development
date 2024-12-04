package com.capstone.skinpal.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var displayName: String? = null,
    var email: String? = null,
    var token: String? = null,
    var photoUrl: String? = null,
    var isLogin: Boolean = false
) : Parcelable
