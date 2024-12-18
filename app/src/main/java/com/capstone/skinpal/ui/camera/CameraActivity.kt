package com.capstone.skinpal.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.ui.ViewModelFactory
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.databinding.ActivityCameraBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.BaseFragment
import com.capstone.skinpal.ui.history.ResultFragment
import com.capstone.skinpal.ui.login.LoginActivity
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity(), BaseFragment {
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference
    private var currentImageUri: Uri? = null
    private val cameraViewModel by viewModels<CameraViewModel> {
        ViewModelFactory(Injection.provideRepository(this))
    }

    private val cropImageLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val croppedImageUri = UCrop.getOutput(result.data!!)
            if (croppedImageUri != null) {
                currentImageUri = croppedImageUri
                showImage()
            } else {
                showToast("Cropping failed.")
            }
        } else {
            showToast("Cropping was cancelled.")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (!granted) {
                Toast.makeText(this, getString(R.string.permission_request_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            observeUserSession()
        }
        userPreference = UserPreference(this)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val week = intent.getStringExtra("WEEK") ?: "test"


        val resultFragment = ResultFragment()

        val bundle = Bundle()
        bundle.putString("week", "test")

        resultFragment.arguments = bundle

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        currentImageUri = savedInstanceState?.getParcelable("CURRENT_IMAGE_URI")
        val imageUriString = intent.getStringExtra("IMAGE_URI")

        if (imageUriString.isNullOrEmpty()) {
            loadPreviewImage(week)
        } else {
            displaySavedImage(imageUriString)
            disableButtons()
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { uri ->
                analyzeImage(uri.toString(), week)
            } ?: showToast("Failed to save image. No image captured.")
        }
    }

    private fun observeUserSession() {
        userPreference = UserPreference(this)
        val session = userPreference.getSession()
        val token = session.token
        if (token == "") {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun loadPreviewImage(week: String) {
        val  userPreference = UserPreference(this)
        val userId = userPreference.getSession().user ?: getString(R.string.default_user)
        cameraViewModel.getImage(userId, week).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.imageUri?.isNotEmpty() == true) {
                        displaySavedImage(result.data.imageUri.toString())
                        disableButtons()
                    }
                }
                else -> Unit
            }
        }
    }

    private fun displaySavedImage(imageUri: String) {
        Glide.with(this)
            .load(imageUri)
            .into(binding.previewImageView)
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                showImage()
                startCrop(it)
            }
        } else {
            currentImageUri = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("CURRENT_IMAGE_URI", currentImageUri)
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(this.cacheDir, "cropped_image_${UUID.randomUUID()}.jpg"))
        val uCropIntent = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1920, 1080)
            .withOptions(UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.JPEG)
                setCompressionQuality(100)
            })
            .getIntent(this)
        cropImageLauncher.launch(uCropIntent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startCrop(uri)
        } else {
            showToast("No image selected.")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Glide.with(this).load(it).into(binding.previewImageView)
        }
    }

    private fun showImageInfo() {
        val week = intent.getStringExtra("WEEK") ?: "test" // Use a default string value
        val  userPreference = UserPreference(this)
        val userId = userPreference.getSession().user ?: getString(R.string.default_user)


        val bottomSheet = ResultFragment().apply {
            arguments = Bundle().apply {
                putString("userId", userId)
                putString("week", "test")
            }
        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun analyzeImage(imageUriString: String, week: String) {
        val userPreference = UserPreference(this)
        val session = userPreference.getSession()

        val user_id = session.user ?: run {
            showToast("User ID not found")
            return
        }

        showLoading(true)
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            if (BuildConfig.DEBUG) {
                Log.d("CameraWeeklyActivity", """
            Uploading image:
            userId: $user_id
            week: $week
            token: ${session.token!!.take(10)}...
            file: ${imageFile.name}
        """.trimIndent())
            }

            cameraViewModel.analyzeImage(
                imageFile = imageFile,
                user_id = user_id,
                week = week
            ).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        showImage()
                        showImageInfo()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        if (result.error.contains("authorized", ignoreCase = true)) {
                            handleApiError(result.error, this)
                            showToast("Session expired. Please login again")
                            // Optional: Navigate to login screen
                        } else {
                            showToast(result.error)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun disableButtons() {
        binding.galleryButton.isEnabled = false
        binding.cameraButton.isEnabled = false
        binding.analyzeButton.isEnabled = false
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
