package com.capstone.skinpal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinpal.R
import com.capstone.skinpal.ui.login.LoginActivity
import com.capstone.skinpal.ui.onboarding.OnboardingActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstLaunch) {
                // Jika pertama kali, arahkan ke OnboardingActivity
                startActivity(Intent(this, OnboardingActivity::class.java))
                sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            } else {
                val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                when {
                    isLoggedIn -> {
                        // User sudah login dan belum logout
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else -> {
                        // User belum login
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
            finish()
        }, 2000) // Durasi splash screen 2 detik
    }
}
