package com.capstone.skinpal.ui.setting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val userPreference: UserPreference by lazy { UserPreference(requireContext()) }
    private val accountViewModel: AccountViewModel by viewModels {
        ViewModelFactory(repository = Injection.provideRepository(requireContext()))
    }

    private val galleryRequestCode = 100
    private val cameraRequestCode = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        // Atur Toolbar sebagai action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        // Ubah properti langsung
        binding.toolbar.title = "About Me"
        binding.toolbar.setTitleTextColor(Color.WHITE)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        val userId = userPreference.getSession()?.user
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
                    .placeholder(R.drawable.icon_person) // Gambar default
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
                val part = MultipartBody.Part.createFormData(
                    "file",
                    "profile_image.jpg",
                    requestBody
                ) // Periksa nama field
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
}
