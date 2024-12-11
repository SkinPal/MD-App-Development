package com.capstone.skinpal.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.ui.login.LoginActivity

interface BaseFragment {
    fun handleTokenExpired(context: Context) {
        val userPreference = UserPreference(context)
        userPreference.logout()

        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }

    fun handleApiError(error: String, context: Context) {
        if (error.contains("401") ||
            error.contains("Unauthorized") ||
            error.contains("Token expired")) {
            handleTokenExpired(context)
        } else {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}