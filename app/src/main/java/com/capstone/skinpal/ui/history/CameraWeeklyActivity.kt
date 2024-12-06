package com.capstone.skinpal.ui.history

import android.Manifest
import android.content.pm.PackageManager
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
import com.bumptech.glide.Glide
import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.ui.ViewModelFactory
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.databinding.ActivityWeeklyCameraBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.camera.ResultFragment
import com.capstone.skinpal.ui.camera.getImageUri
import com.capstone.skinpal.ui.camera.reduceFileImage
import com.capstone.skinpal.ui.camera.uriToFile

class CameraWeeklyActivity : AppCompatActivity() {
    private var _binding: ActivityWeeklyCameraBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private val cameraWeeklyViewModel by viewModels<CameraWeeklyViewModel> {
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
        _binding = ActivityWeeklyCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val week = intent.getStringExtra("WEEK") ?: "pekan1" // Use a default string value
        title = "Week $week"

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
                //saveImage(uri.toString(), week)
                showImageInfo()
                analyzeImage(uri.toString(), week)
            } ?: showToast("Failed to save image. No image captured.")
        }
    }

    private fun loadPreviewImage(week: String) {
        cameraWeeklyViewModel.getImage(week).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.image.isNotEmpty()) {
                        displaySavedImage(result.data.image)
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
            .withMaxResultSize(224, 224)
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
        currentImageUri?.let { uri ->
            // Create an instance of ResultFragment
            val bottomSheet = ResultFragment()

            val bundle = Bundle()
            bundle.putString("imageUri", uri.toString()) // Example of passing data
            bottomSheet.arguments = bundle

            // Show the ResultFragment as a BottomSheet
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /*private fun saveImage(imageUriString: String, week: String) {
        val userPreference = UserPreference(this)
        val session = userPreference.getSession()

        val user_id = session.user ?: run {
            showToast("User ID not found")
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            // Add logging for debugging
            if (BuildConfig.DEBUG) {
                Log.d("CameraWeeklyActivity", """
                Uploading image:
                userId: $user_id
                week: $week
                token: ${session.token!!.take(10)}...
                file: ${imageFile.name}
            """.trimIndent())
            }

            cameraWeeklyViewModel.uploadImage(
                imageFile = imageFile,
                user_id = user_id,
                week = week.toString()
            ).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        showToast("Image uploaded successfully")
                    }
                    is Result.Error -> {
                        showLoading(false)
                        if (result.error.contains("authorized", ignoreCase = true)) {
                            showToast("Session expired. Please login again")
                            // Optional: Navigate to login screen
                        } else {
                            showToast("Debug - Saved Token: ${session.token?.take(10) ?: "null"}")
                            showToast(result.error)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }*/

    private fun analyzeImage(imageUriString: String, week: String) {
        val userPreference = UserPreference(this)
        val session = userPreference.getSession()

        val user_id = session.user ?: run {
            showToast("User ID not found")
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            // Add logging for debugging
            if (BuildConfig.DEBUG) {
                Log.d("CameraWeeklyActivity", """
                Uploading image:
                userId: $user_id
                week: $week
                token: ${session.token!!.take(10)}...
                file: ${imageFile.name}
            """.trimIndent())
            }

            cameraWeeklyViewModel.analyzeImage(
                imageFile = imageFile,
                user_id = user_id,
                week = week.toString()
            ).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        if (result.error.contains("authorized", ignoreCase = true)) {
                            showToast("Session expired. Please login again")
                            // Optional: Navigate to login screen
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
