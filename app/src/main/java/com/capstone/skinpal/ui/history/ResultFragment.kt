package com.capstone.skinpal.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.databinding.FragmentResultBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var userPreference: UserPreference
    private val cameraWeeklyViewModel: CameraWeeklyViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference(requireContext())

        // Retrieve userId and week from session and arguments
        val session = userPreference.getSession()
        val userId = session.user ?: getString(R.string.default_user)
        val week = arguments?.getString("week") ?: getString(R.string.default_week)

        setupRecyclerView()
        observeAnalysis()
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter()
        binding.sunscreenRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = resultAdapter
        }
    }

    private fun observeAnalysis() {

        val week = arguments?.getString("week") ?: getString(R.string.default_week)
        val user_id = userPreference.getSession().user.toString()
        cameraWeeklyViewModel.getResult().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val analysis = result.data
                    if (analysis != null) {
                        displayResult(analysis)
                    } else {
                        //showDefaultResult()
                    }
                }
                is Result.Error -> {
                    showError(result.error)
                }
                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun displayResult(analysis: AnalysisEntity) {
        showLoading(false)
        binding.skinTypeValue.text = analysis.skinType
        binding.acneValue.text = analysis.acne
        binding.rednessValue.text = analysis.redness
        binding.wrinklesValue.text = analysis.wrinkles

       // val recommendations = analysis.sunscreenItem ?: emptyList()
        //resultAdapter.submitList(recommendations as List<MoisturizerItem?>?)

        // Update the RecyclerView data
       // val recommendations = analysis.recommendations ?: emptyList()
        //resultAdapter.submitList(recommendations)
        Log.d("ResultFragment", "Analysis: $analysis")
    }


    fun Float.toPercent(): String {
        return String.format("%.2f%%", this * 100) // Mengalikan nilai dengan 100 dan menambahkan simbol %
    }
    /*private fun showDefaultResult() {
        showLoading(false)
        binding.skinConditions.text = getString(R.string.no_skin_conditions)
        binding.skinType.text = getString(R.string.no_skin_type)
        resultAdapter.submitList(emptyList())
    }*/

    private fun showError(error: String) {
        showLoading(false)
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
