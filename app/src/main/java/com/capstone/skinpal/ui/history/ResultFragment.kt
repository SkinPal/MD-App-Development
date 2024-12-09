package com.capstone.skinpal.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.databinding.FragmentResultBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.product.ProductAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var sunscreenAdapter: SunscreenAdapter
    private lateinit var moisturizerAdapter: MoisturizerAdapter
    private lateinit var tonerAdapter: TonerAdapter
    private lateinit var serumAdapter: SerumAdapter
    private lateinit var facialWashAdapter: FacialWashAdapter
    private lateinit var treatmentAdapter: TreatmentAdapter
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

        setToggleVisibility(binding.moisturizer, binding.moisturizerRecycler, R.drawable.icon_more, R.drawable.icon_close)
        setToggleVisibility(binding.serum, binding.serumRecycler, R.drawable.icon_more, R.drawable.icon_close)
        setToggleVisibility(binding.toner, binding.tonerRecycler, R.drawable.icon_more, R.drawable.icon_close)
        setToggleVisibility(binding.treatment, binding.treatmentRecycler, R.drawable.icon_more, R.drawable.icon_close)
        setToggleVisibility(binding.facialWash, binding.facialWashRecycler, R.drawable.icon_more, R.drawable.icon_close)
        setToggleVisibility(binding.sunscreen, binding.sunscreenRecycler, R.drawable.icon_more, R.drawable.icon_close)

        setupRecyclerView()
        observeAnalysis()
    }

    private fun setToggleVisibility(header: TextView, recyclerView: RecyclerView, expandIcon: Int, collapseIcon: Int) {
        header.setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE) {
                recyclerView.visibility = View.GONE
                header.setCompoundDrawablesWithIntrinsicBounds(0, 0, expandIcon, 0)
            } else {
                recyclerView.visibility = View.VISIBLE
                header.setCompoundDrawablesWithIntrinsicBounds(0, 0, collapseIcon, 0)
            }
        }
    }

    private fun setupRecyclerView() {
        sunscreenAdapter = SunscreenAdapter()
        binding.sunscreenRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sunscreenAdapter
        }

        moisturizerAdapter = MoisturizerAdapter()
        binding.moisturizerRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = moisturizerAdapter
        }

        tonerAdapter = TonerAdapter()
        binding.tonerRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tonerAdapter
        }

        serumAdapter = SerumAdapter()
        binding.serumRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = serumAdapter
        }

        treatmentAdapter = TreatmentAdapter()
        binding.treatmentRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = treatmentAdapter
        }

        facialWashAdapter = FacialWashAdapter()
        binding.facialWashRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = facialWashAdapter
        }
    }

    private fun observeAnalysis() {

        val week = arguments?.getString("week") ?: getString(R.string.default_week)
        val user_id = userPreference.getSession().user.toString()
        cameraWeeklyViewModel.getAnalysis(user_id, week).observe(viewLifecycleOwner) { result ->
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

    private fun displayResult(analysis: AnalysisResult) {
        showLoading(false)
        binding.skinTypeValue.text = analysis.skinType
        binding.acneValue.text = analysis.acne
        binding.rednessValue.text = analysis.redness
        binding.wrinklesValue.text = analysis.wrinkles

       // val recommendations = analysis.sunscreenItem ?: emptyList()
        val moisturizerEntities = analysis.moisturizer?.map { moisturizerItem ->
            ProductEntity( // Map fields appropriately
                name = moisturizerItem.name,
                imageUrl = moisturizerItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        moisturizerAdapter.submitList(moisturizerEntities)

        val sunscreenEntities = analysis.sunscreen?.map { sunscreenItem ->
            ProductEntity( // Map fields appropriately
                name = sunscreenItem.name,
                imageUrl = sunscreenItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        sunscreenAdapter.submitList(sunscreenEntities)

        val tonerEntities = analysis.toner?.map { tonerItem ->
            ProductEntity( // Map fields appropriately
                name = tonerItem.name,
                imageUrl = tonerItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        tonerAdapter.submitList(tonerEntities)

        val serumEntities = analysis.serum?.map { serumItem ->
            ProductEntity( // Map fields appropriately
                name = serumItem.name,
                imageUrl = serumItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        serumAdapter.submitList(serumEntities)

        val facialWashEntities = analysis.facialWash?.map { facialWashItem ->
            ProductEntity( // Map fields appropriately
                name = facialWashItem.name,
                imageUrl = facialWashItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        facialWashAdapter.submitList(facialWashEntities)

        val treatmentEntities = analysis.treatment?.map { treatmentItem ->
            ProductEntity( // Map fields appropriately
                name = treatmentItem.name,
                imageUrl = treatmentItem.imageUrl
                // Add any other required fields
            )
        } ?: emptyList()

        treatmentAdapter.submitList(treatmentEntities)

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
