package com.capstone.skinpal.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        binding.uploadPhoto0.setOnClickListener { cameraPreview0() }
        binding.uploadPhoto1.setOnClickListener { cameraPreview1() }
        binding.uploadPhoto2.setOnClickListener { cameraPreview2() }
        binding.uploadPhoto3.setOnClickListener { cameraPreview3() }
        binding.uploadPhoto4.setOnClickListener { cameraPreview4() }
    }
    private fun cameraPreview0() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java)
        intent.putExtra("WEEK", "pekan0")
        startActivity(intent)
    }
    private fun cameraPreview1() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java)
        intent.putExtra("WEEK", "pekan1")
        startActivity(intent)
    }
    private fun cameraPreview2() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java)
        intent.putExtra("WEEK", "pekan2")
        startActivity(intent)
    }
    private fun cameraPreview3() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java)
        intent.putExtra("WEEK", "pekan3")
        startActivity(intent)
    }
    private fun cameraPreview4() {
        val intent = Intent(requireContext(), CameraWeeklyActivity::class.java)
        intent.putExtra("WEEK", "pekan4")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding reference to avoid memory leaks
    }
}
