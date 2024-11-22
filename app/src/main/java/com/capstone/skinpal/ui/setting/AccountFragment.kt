package com.capstone.skinpal.ui.setting

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.capstone.skinpal.databinding.FragmentAccountBinding
import com.capstone.skinpal.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set up Logout Button
        binding.buttonLogout.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        lifecycleScope.launch {
            // Clear FirebaseAuth session
            auth.signOut()

            // Clear Credential Manager state
            val credentialManager = CredentialManager.create(requireContext())
            credentialManager.clearCredentialState(ClearCredentialStateRequest())

            // Navigate to LoginActivity
            startActivity(Intent(requireContext(), LoginActivity::class.java))

            // Finish the current activity
            requireActivity().finish()
        }
    }
}
