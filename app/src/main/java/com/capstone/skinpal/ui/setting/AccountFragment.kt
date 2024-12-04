package com.capstone.skinpal.ui.setting

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.databinding.FragmentAccountBinding
import com.capstone.skinpal.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid)
            fetchUserData(currentUser)
        } else {
            Log.e("Firebase", "No authenticated user found.")
        }

        // Set click listener for updating profile photo
        binding.photoProfile.setOnClickListener { openImagePicker() }

        // Set click listener for logout
        binding.logoutButton.setOnClickListener { signOut() }

        return binding.root
    }

    private fun fetchUserData(user: FirebaseUser) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userInfo = snapshot.getValue(UserModel::class.java)
                if (userInfo != null) {
                    binding.nama.text = userInfo.user ?: "No Name"

                    // Check if the fragment is added before loading the image
                    /*if (isAdded) {
                        Glide.with(this@AccountFragment)
                            .load(userInfo.)
                            .into(binding.photoProfile)
                    } else {
                        Log.e("Glide", "Fragment is not attached to activity.")
                    }*/
                } else {
                    Log.e("Firebase", "User data is null.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch user data: ${error.message}")
            }
        })
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { imageUri ->
                binding.photoProfile.setImageURI(imageUri)
                uploadImageToFirebase(imageUri)
            } ?: Log.e("Firebase", "Image URI is null.")
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        storageReference = FirebaseStorage.getInstance().getReference("Users/$uid/profile.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    databaseReference.child("photoUrl").setValue(imageUrl)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Profile photo updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Failed to update profile photo: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to upload image: ${e.message}")
            }
    }

    private fun signOut() {
        lifecycleScope.launch {
            auth.signOut()
            try {
                val credentialManager = CredentialManager.create(requireContext())
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("CredentialManager", "Failed to clear credential state: ${e.message}")
            }
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
