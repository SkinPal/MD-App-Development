package com.capstone.skinpal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinpal.R
import com.capstone.skinpal.ui.login.LoginActivity
import com.capstone.skinpal.ui.register.RegisterActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isLoggedIn -> {
                    // User sudah login dan belum logout
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else -> {
                    // User sudah mendaftar tetapi belum login
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            finish()
        }, 2000) // Durasi splash screen 2 detik
    }
}

