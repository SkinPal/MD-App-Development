package com.capstone.skinpal.ui.history

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.ui.ViewModelFactory
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID
import kotlin.getValue
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.databinding.ActivityWeeklyCameraBinding
import com.capstone.skinpal.ui.camera.getImageUri

class CameraWeeklyActivity : AppCompatActivity() {
    private var binding: ActivityWeeklyCameraBinding? = null
    private var currentImageUri: Uri? = null
    private val cameraWeeklyViewModel by viewModels<CameraWeeklyViewModel> {
        ViewModelFactory.Companion.getInstance(application)
    }

    private val cropImageLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                val croppedImageUri = UCrop.getOutput(intent)
                if (croppedImageUri != null) {
                    currentImageUri = croppedImageUri
                    showImage()
                } else {
                    showToast("Cropping failed.")
                }
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
        binding = ActivityWeeklyCameraBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val week = intent.getIntExtra("WEEK", 1)
        setTitle("Week $week")
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        currentImageUri = savedInstanceState?.getParcelable("CURRENT_IMAGE_URI")
        showImage()
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString.isNullOrEmpty()) {
            showImage()
        } else {
            showSavedImage(imageUriString, week)
            disableButtons()
        }

        binding?.galleryButton?.setOnClickListener { startGallery() }
        binding?.cameraButton?.setOnClickListener { startCamera() }
        binding?.saveButton?.setOnClickListener {
            // Only save if the image URI is valid
            currentImageUri?.let { uri ->
                saveImage(uri.toString(), week)
            } ?: showToast("Failed to save image. No image captured.")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
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
            currentImageUri = uri
            showImage()
            startCrop(uri)
        } else {
            showToast("No image selected.")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding?.previewImageView?.setImageURI(it)
        }
    }

    private fun showSavedImage(imageUriString: String, week: Int) {
        cameraWeeklyViewModel.getImage(week).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val imageUri = result.data // Adjust this if your result is different
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding?.previewImageView!!)
                }
                is Result.Error -> {
                    showToast("Error loading image.")
                }
                is Result.Loading -> {
                    showToast("Loading image...")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveImage(imageUriString: String, week: Int) {
        if (imageUriString.isNotEmpty()) {
            val imageEntity = ImageEntity(image = imageUriString, week = week)
            cameraWeeklyViewModel.saveItem(imageEntity)
            showToast("Image saved successfully!")
            disableButtons()  // Disable buttons after saving
        } else {
            showToast("Failed to save image. Image URI is invalid.")
        }
    }

    private fun disableButtons() {
        binding?.galleryButton?.isEnabled = false
        binding?.cameraButton?.isEnabled = false
        binding?.saveButton?.isEnabled = false
    }

    private fun enableButtons() {
        binding?.galleryButton?.isEnabled = true
        binding?.cameraButton?.isEnabled = true
        binding?.saveButton?.isEnabled = true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        const val EXTRA_IMAGE_ID = "extra_image_id"
    }
}
