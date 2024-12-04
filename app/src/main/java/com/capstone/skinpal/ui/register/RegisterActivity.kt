package com.capstone.skinpal.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinpal.R
import com.capstone.skinpal.databinding.ActivityRegisterBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.ui.ViewModelFactory
import kotlin.getValue

class RegisterActivity : AppCompatActivity() {

    private var confirmationDialog: AlertDialog? = null
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory(Injection.provideRepository(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        binding.edRegisterName.addTextChangedListener(inputWatcher)
        binding.edRegisterUid.addTextChangedListener(inputWatcher)
        binding.edRegisterEmail.addTextChangedListener(inputWatcher)
        binding.edRegisterPassword.addTextChangedListener(inputWatcher)

        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val user_id = binding.edRegisterUid.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            registerViewModel.register(user_id, name, email, password)
            registerViewModel.registrationResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Success -> {
                    showLoading(false)
                    showConfirmationDialog(binding.edRegisterEmail.text.toString())
                }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private val inputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateButtonState()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateButtonState() {
        val name = binding.edRegisterName.text
        val user_id = binding.edRegisterUid.text
        val email = binding.edRegisterEmail.text
        val password = binding.edRegisterPassword.text
        binding.signupButton.isEnabled = !name.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }


    private fun showConfirmationDialog(email: String) {
        val message = getString(R.string.account_created_message, email)
        confirmationDialog = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(message)
            setPositiveButton("Continue") { _, _ ->
                confirmationDialog?.dismiss()
                finish()
            }
        }.create()

        confirmationDialog?.show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val usernameTextView =
            ObjectAnimator.ofFloat(binding.userIdTextView, View.ALPHA, 1f).setDuration(100)
        val usernameEditTextLayout =
            ObjectAnimator.ofFloat(binding.userIdEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                usernameTextView,
                usernameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}