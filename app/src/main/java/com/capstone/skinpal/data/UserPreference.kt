package com.capstone.skinpal.data

import android.content.Context
import kotlin.math.E

class UserPreference(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(NAME, value.displayName ?: "")
        editor.putString(EMAIL, value.email?: "")
        editor.putString(PHOTO_URL, value.photoUrl?: "")
        editor.putString(TOKEN, value.token ?: "")
        editor.putBoolean(IS_LOGIN, value.isLogin)
        editor.apply()
    }

    fun getSession(): UserModel {
        val model = UserModel()
        model.displayName = preferences.getString(NAME, "") ?: ""
        model.email = preferences.getString(EMAIL, "") ?: ""
        model.photoUrl = preferences.getString(PHOTO_URL, "") ?: ""
        model.token = preferences.getString(TOKEN, "") ?: ""
        model.isLogin = preferences.getBoolean(IS_LOGIN, false)
        return model
    }

    fun logout() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PHOTO_URL = "photoUrl"
        private const val TOKEN = "token"
        private const val IS_LOGIN = "is_login"
    }
}
