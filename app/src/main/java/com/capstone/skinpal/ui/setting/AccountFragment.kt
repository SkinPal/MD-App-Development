package com.capstone.skinpal.ui.setting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.capstone.skinpal.R
import com.capstone.skinpal.databinding.FragmentAccountBinding
import com.capstone.skinpal.ui.login.LoginActivity
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var settingViewModel: SettingViewModel
    private val userPreference: UserPreference by lazy { UserPreference(requireContext()) }
    private val accountViewModel: AccountViewModel by viewModels {
        ViewModelFactory(repository = Injection.provideRepository(requireContext()))
    }
    private lateinit var workManager: WorkManager

    private val galleryRequestCode = 100
    private val cameraRequestCode = 101

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(),
                    getString(R.string.notifications_permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.notifications_permission_rejected), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        binding.toolbar.title = "About Me"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(pref)
        )[SettingViewModel::class.java]
        workManager = WorkManager.getInstance(requireContext())

        setupUI()
        observeViewModel()
        setupSettings()

        return binding.root
    }

    private fun setupSettings() {
             binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                scheduleNotificationsAt(listOf(
                    Pair(10, 0),  // 6:00 AM
                    Pair(22, 15) // 10:15 PM
                ))
            } else {
                cancelNotificationTasks()
            }
        }
        settingViewModel.getNotificationSetting().observe(viewLifecycleOwner) { isNotificationActive ->
            binding.switchNotifications.isChecked = isNotificationActive
        }
    }

    private fun setupUI() {
        val userId = userPreference.getSession().user
        if (!userId.isNullOrEmpty()) {
            accountViewModel.fetchUserProfile()
        } else {
            Log.e("AccountFragment", "User ID kosong atau belum login")
        }

        binding.logoutButton.setOnClickListener { signOut() }
        binding.ivCamera.setOnClickListener { showImagePickerDialog() }

    }

    private fun observeViewModel() {
        accountViewModel.userProfile.observe(viewLifecycleOwner) { profileResponse ->
            profileResponse?.data?.let { data ->
                binding.tvName.text = data.nama
                binding.tvEmail.text = data.email
                binding.tvUsername.text = data.username
                Glide.with(requireContext())
                    .load(data.profileImage)
                    .placeholder(R.drawable.icon_person)
                    .into(binding.photoProfile)
            }
        }

        accountViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun signOut() {
        accountViewModel.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil Foto Baru", "Pilih dari Galeri")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Foto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Izin Kamera Diperlukan")
            .setMessage("Aplikasi memerlukan akses kamera untuk mengambil foto profil.")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, galleryRequestCode)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, cameraRequestCode)
        } else {
            Log.e("AccountFragment", "Tidak ada aplikasi kamera yang tersedia")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                galleryRequestCode -> {
                    val imageUri = data.data
                    imageUri?.let { handleImageUpload(it) }
                }

                cameraRequestCode -> {
                    val bitmap = data.extras?.get("data") as? Bitmap
                    bitmap?.let { handleBitmapUpload(it) }
                }
            }
        }
    }

    private fun handleImageUpload(uri: Uri) {
        lifecycleScope.launch {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.let {
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it.readBytes())
                // Periksa nama field
                val part = MultipartBody.Part.createFormData("file", "profile_image.jpg", requestBody)
                accountViewModel.uploadProfile(part)
                Glide.with(requireContext()).load(uri).into(binding.photoProfile)
            }
        }
    }

    private fun handleBitmapUpload(bitmap: Bitmap) {
        lifecycleScope.launch {
            val byteArrayOutputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val requestBody = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                byteArrayOutputStream.toByteArray()
            )
            val part = MultipartBody.Part.createFormData(
                "file",
                "profile_image.jpg",
                requestBody
            ) // Periksa nama field
            accountViewModel.uploadProfile(part)
            Glide.with(requireContext()).load(bitmap).into(binding.photoProfile)
        }
    }

    fun scheduleNotificationsAt(times: List<Pair<Int, Int>>) {
        for ((hour, minute) in times) {
            val currentTime = Calendar.getInstance()
            val notificationTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            // If the notification time is before the current time, schedule for the next day
            if (notificationTime.before(currentTime)) {
                notificationTime.add(Calendar.DAY_OF_MONTH, 1)
            }

            val delay = notificationTime.timeInMillis - currentTime.timeInMillis

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(notificationWorkRequest)
        }
    }

    private fun cancelNotificationTasks() {
        workManager.cancelUniqueWork("EventNotificationWork")
    }
}