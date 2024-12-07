package com.capstone.skinpal.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinpal.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.MainActivity
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.register.RegisterActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(Injection.provideRepository(this))
    }
    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        auth = Firebase.auth

        binding.registerButton.setOnClickListener {
            Intent(this, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }.also { startActivity(it) }
        }
        //userPreference = UserPreference(this)

        setupView()
        setupAction()
        playAnimation()
        checkGooglePlayServices()

        binding.edLoginUserId.addTextChangedListener(inputWatcher)
        binding.edLoginPassword.addTextChangedListener(inputWatcher)


    }

    private fun checkGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Log.e("GooglePlayServices", "This device is not supported.")
                finish()
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.edLoginUserId.addTextChangedListener(inputWatcher)
        binding.edLoginPassword.addTextChangedListener(inputWatcher)

        binding.loginButton.setOnClickListener {
            val user_id = binding.edLoginUserId.text.toString()
            val password = binding.edLoginPassword.text.toString()

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            if (isConnectedToInternet()) {
                loginViewModel.login(user_id, password)
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            }

            observeSession()

            /*if (validateInput(userId, password)) {
                if (isConnectedToInternet()) {
                    showLoading(true)
                    loginViewModel.login(userId, password)
                    navigateToMainActivity()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.no_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/
        }
    }

    private fun observeSession() {
        loginViewModel.loginResult.observe(this) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<UserModel>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                val token = result.data.token
                Log.d(TAG, "Login successful. Token: $token")
                loginViewModel.saveSession(result.data)
                navigateToMainActivity()
            }

            is Result.Error -> {
                showLoading(false)
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private val inputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateButtonState()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateButtonState() {
        val user_id = binding.edLoginUserId.text
        val password = binding.edLoginPassword.text
        binding.loginButton.isEnabled = !user_id.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val userIdTextView = ObjectAnimator.ofFloat(binding.userIdTextView, View.ALPHA, 1f).setDuration(100)
        val userIdEditTextLayout = ObjectAnimator.ofFloat(binding.userIdEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title, userIdTextView, userIdEditTextLayout, passwordTextView, passwordEditTextLayout, login
            )
            startDelay = 100
        }.start()
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
