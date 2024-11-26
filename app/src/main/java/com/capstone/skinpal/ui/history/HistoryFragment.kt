package com.capstone.skinpal.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Properly inflate the binding
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener
        binding.uploadPhoto.setOnClickListener {
            cameraPreview()
        }
    }

    private fun cameraPreview() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java).apply {
            // Pass an extra if needed (adjust logic if `ImageEntity` is necessary)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding reference to avoid memory leaks
    }
}
