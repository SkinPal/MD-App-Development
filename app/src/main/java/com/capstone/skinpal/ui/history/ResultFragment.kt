package com.capstone.skinpal.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.FragmentResultBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
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

        val session = userPreference.getSession()
        session.user ?: getString(R.string.default_user)
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
        val week = arguments?.getString("week") ?: getString(R.string.default_week)
        if (week == "pekan4") {
            binding.skinTypeValue.text = analysis.skinType
            binding.acneValue.text = analysis.acne
            binding.rednessValue.text = analysis.redness
            binding.wrinklesValue.text = analysis.wrinkles

            binding.progressPercentage.visibility = View.VISIBLE
            binding.progressMessage.visibility = View.VISIBLE

            binding.progress.visibility = View.VISIBLE
            binding.progressPercentage.text = analysis.percentage
            binding.progressMessage.text = analysis.message
        } else {
            binding.skinTypeValue.visibility = View.VISIBLE
            binding.acneValue.visibility = View.VISIBLE
            binding.rednessValue.visibility = View.VISIBLE
            binding.wrinklesValue.visibility = View.VISIBLE

            binding.progressPercentage.visibility = View.GONE
            binding.progressMessage.visibility = View.GONE

            binding.skinTypeValue.text = analysis.skinType
            binding.acneValue.text = analysis.acne
            binding.rednessValue.text = analysis.redness
            binding.wrinklesValue.text = analysis.wrinkles
        }

        val moisturizerEntities = analysis.moisturizer.map { moisturizerItem ->
            ProductEntity(
                name = moisturizerItem.name,
                imageUrl = moisturizerItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        moisturizerAdapter.submitList(moisturizerEntities)

        val sunscreenEntities = analysis.sunscreen.map { sunscreenItem ->
            ProductEntity(
                name = sunscreenItem.name,
                imageUrl = sunscreenItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        sunscreenAdapter.submitList(sunscreenEntities)

        val tonerEntities = analysis.toner.map { tonerItem ->
            ProductEntity(
                name = tonerItem.name,
                imageUrl = tonerItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        tonerAdapter.submitList(tonerEntities)

        val serumEntities = analysis.serum.map { serumItem ->
            ProductEntity(
                name = serumItem.name,
                imageUrl = serumItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        serumAdapter.submitList(serumEntities)

        val facialWashEntities = analysis.facialWash.map { facialWashItem ->
            ProductEntity(
                name = facialWashItem.name,
                imageUrl = facialWashItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        facialWashAdapter.submitList(facialWashEntities)

        val treatmentEntities = analysis.treatment.map { treatmentItem ->
            ProductEntity(
                name = treatmentItem.name,
                imageUrl = treatmentItem.imageUrl,
                description = null,
                ingredients = null,
                isBookmarked = null,
                type = null
            )
        }

        treatmentAdapter.submitList(treatmentEntities)
        Log.d("ResultFragment", "Analysis: $analysis")
    }

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
