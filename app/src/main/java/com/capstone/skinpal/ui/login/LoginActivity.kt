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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.capstone.skinpal.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.MainActivity
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.register.RegisterActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

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
        auth = Firebase.auth

        binding.signInButton.setOnClickListener {
            signIn()
        }

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

    private fun signIn() {
        val credentialManager = CredentialManager.create(this) //import from androidx.CredentialManager
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "Received ID Token: $idToken")
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                    saveUserData(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun saveUserData(user: FirebaseUser?) {
        if (user != null) {
            val database = FirebaseDatabase.getInstance().reference
            val userId = user.uid
            val userData = UserInfo(user.displayName, user.email)

            // Save user data under their UID
            database.child("users").child(userId).setValue(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "User data saved successfully")
                    } else {
                        Log.e("Firebase", "Failed to save user data: ${task.exception?.message}")
                    }
                }
        }
    }

    data class UserInfo(val displayName: String?, val email: String?)


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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (validateInput(email, password)) {
                if (isConnectedToInternet()) {
                    // Extract userId from email (assuming email is used as userId)
                    val userId = email.substringBefore("@")
                    loginViewModel.login(userId, password)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.no_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                   // hideLoading()
                    // Handle successful login
                    val response = result.data
                    // Save user data if needed
                   // navigateToMain()
                }
                is Result.Error -> {
                   // hideLoading()
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    showLoading(
                        isLoading = TODO()
                    )
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = "Email cannot be empty"
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordEditTextLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    /*private fun observeSession() {
        loginViewModel.loginResult.observe(this) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<UserModel>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                loginViewModel.saveSession(result.data)
                navigateToStoryActivity()
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    private fun navigateToStoryActivity() {
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
        val email = binding.edLoginEmail.text
        val password = binding.edLoginPassword.text
        binding.loginButton.isEnabled = !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
         val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, login
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
