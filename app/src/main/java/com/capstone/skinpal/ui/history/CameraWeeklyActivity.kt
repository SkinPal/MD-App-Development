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
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.databinding.ActivityWeeklyCameraBinding
import com.capstone.skinpal.ui.camera.CameraActivity
import com.capstone.skinpal.ui.camera.ResultFragment
import com.capstone.skinpal.ui.camera.getImageUri

class CameraWeeklyActivity : AppCompatActivity() {
    private var _binding: ActivityWeeklyCameraBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private val cameraWeeklyViewModel by viewModels<CameraWeeklyViewModel> {
        ViewModelFactory.Companion.getInstance(application)
    }

    private val cropImageLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val croppedImageUri = UCrop.getOutput(result.data!!)
            if (croppedImageUri != null) {
                currentImageUri = croppedImageUri
                showImage()
                showImageInfo()
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

        val week = intent.getIntExtra("WEEK", 1)
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
        binding.saveButton.setOnClickListener {
            currentImageUri?.let { uri ->
                saveImage(uri.toString(), week)
            } ?: showToast("Failed to save image. No image captured.")
        }
    }

    private fun loadPreviewImage(week: Int) {
        cameraWeeklyViewModel.getImage(week).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.image.isNotEmpty()) {
                        displaySavedImage(result.data.image)
                        disableButtons()
                    }
                }
                is Result.Error -> showToast("Error loading saved image.")
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
            val imageName = uri.lastPathSegment ?: "Unknown Image"
            val additionalInfo = "This image is displayed from the URI: $uri"
            val bottomSheet = ResultFragment.newInstance(imageName, additionalInfo)
            bottomSheet.show(supportFragmentManager, ResultFragment::class.java.simpleName)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveImage(imageUriString: String, week: Int) {
        val imageEntity = ImageEntity(image = imageUriString, week = week)
        cameraWeeklyViewModel.saveItem(imageEntity)
        showToast("Image saved successfully!")
        disableButtons()
    }

    private fun disableButtons() {
        binding.galleryButton.isEnabled = false
        binding.cameraButton.isEnabled = false
        binding.saveButton.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
