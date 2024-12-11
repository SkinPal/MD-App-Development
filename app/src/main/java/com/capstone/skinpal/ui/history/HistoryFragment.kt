package com.capstone.skinpal.ui.history

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.databinding.FragmentHistoryBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.BaseFragment
import com.capstone.skinpal.ui.ViewModelFactory
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.getValue

class HistoryFragment : Fragment(), BaseFragment {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference
    private val viewLifecycleScope = lazy { viewLifecycleOwner.lifecycleScope }
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

        userPreference = UserPreference(requireContext())
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

        viewLifecycleOwner.lifecycleScope.launch {
            cameraWeeklyViewModel.getAnalysis(userId, week).observe(viewLifecycleOwner) { result ->
                if (!isAdded || _binding == null) return@observe

                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        result.data.let { data ->
                            updateUIForWeek(week, data)
                        }
                    }
                    is Result.Error -> {
                        handleApiError(result.error, requireContext())
                        showLoading(false)
                    }
                    is Result.Loading -> showLoading(true)
                }
            }
        }
    }

    private fun updateUIForWeek(week: String, data: AnalysisResult) {
        if (!isAdded || _binding == null) return

        val button = when (week) {
            "pekan0" -> binding.uploadPhoto0
            "pekan1" -> binding.uploadPhoto1
            "pekan2" -> binding.uploadPhoto2
            "pekan3" -> binding.uploadPhoto3
            "pekan4" -> binding.uploadPhoto4
            else -> null
        }

        button?.apply {
            text = "View Result"
            setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)))
        }

        when (week) {
            "pekan0" -> binding.checkboxWeek0
            "pekan1" -> binding.checkboxWeek1
            "pekan2" -> binding.checkboxWeek2
            "pekan3" -> binding.checkboxWeek3
            "pekan4" -> binding.checkboxWeek4
            else -> null
        }?.setImageResource(R.drawable.baseline_check_box_24)
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.let { binding ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
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
        viewLifecycleScope.value.cancel()
    }
}
