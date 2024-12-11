package com.capstone.skinpal.ui.history

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.databinding.FragmentHistoryBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val cameraWeeklyViewModel: CameraWeeklyViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireActivity()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //checkAllWeekAnalysis()
        binding.uploadPhoto0.setOnClickListener { cameraPreview0() }
        binding.uploadPhoto1.setOnClickListener { cameraPreview1() }
        binding.uploadPhoto2.setOnClickListener { cameraPreview2() }
        binding.uploadPhoto3.setOnClickListener { cameraPreview3() }
        binding.uploadPhoto4.setOnClickListener { cameraPreview4() }
    }

    override fun onResume() {
        super.onResume()
        checkAllWeekAnalysis()
    }

    private fun checkAllWeekAnalysis() {
        val userPreference = UserPreference(requireContext())
        val userId = userPreference.getSession().user ?: getString(R.string.default_user)

        // List of weeks to check
        val weeks = listOf("pekan0", "pekan1", "pekan2", "pekan3", "pekan4")

        weeks.forEach { week ->
            checkWeekAnalysis(userId, week)
        }
    }

    private fun checkWeekAnalysis(userId: String, week: String) {
        Log.d("CameraWeeklyActivity", "Loading preview for userId: $userId, week: $week")

        cameraWeeklyViewModel.getAnalysis(userId, week).observe(requireContext() as LifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    updateUIForWeek(week, result.data)
                }
                is Result.Error -> {
                    showLoading(false)

                    Log.e("CameraWeeklyActivity", "Error loading preview for $week: ${result.error}")
                }
                is Result.Loading -> showLoading(true)
            }
        }
    }

    private fun updateUIForWeek(week: String, data: AnalysisResult?) {
        val button = when (week) {
            "pekan0" -> binding.uploadPhoto0.apply {
                text = "View Result"
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
            }
            "pekan1" -> binding.uploadPhoto1.apply {
                text = "View Result"
                setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
            }
            "pekan2" -> binding.uploadPhoto2.apply {
                text = "View Result"
                setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
            }
            "pekan3" -> binding.uploadPhoto3.apply {
                text = "View Result"
                setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
            }
            "pekan4" -> binding.uploadPhoto4.apply {
                text = "View Result"
                setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
            }
            else -> null
        }


        val imageView = when (week) {
            "pekan0" -> binding.checkboxWeek0.setImageResource(R.drawable.baseline_check_box_24)
            "pekan1" -> binding.checkboxWeek1.setImageResource(R.drawable.baseline_check_box_24)
            "pekan2" -> binding.checkboxWeek2.setImageResource(R.drawable.baseline_check_box_24)
            "pekan3" -> binding.checkboxWeek3.setImageResource(R.drawable.baseline_check_box_24)
            "pekan4" -> binding.checkboxWeek4.setImageResource(R.drawable.baseline_check_box_24)
            else -> null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        _binding = null
    }
}
