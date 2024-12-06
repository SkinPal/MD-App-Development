package com.capstone.skinpal.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.skinpal.data.remote.response.AnalyzeResponse
import com.capstone.skinpal.data.remote.response.BasicRoutine
import com.capstone.skinpal.databinding.BottomSheetResultBinding
import com.capstone.skinpal.ui.product.ProductAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.capstone.skinpal.R
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.ui.product.Converters

class ResultFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetResultBinding? = null
    private val binding get() = _binding!!

    private var analyzeResponse: AnalyzeResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the analysis result from arguments
        analyzeResponse = arguments?.getParcelable("ANALYZE_RESPONSE")

        // Handle case when no response is provided
        if (analyzeResponse == null) {
            showError("No analysis data available")
            return
        }

        // Proceed to display the results if available
        analyzeResponse?.let { response ->
            displayResults(response)
        }

        // Set up the close button
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun saveToDatabase(response: AnalyzeResponse) {
        val converters = Converters()

        val skinAnalysis = AnalysisEntity(
            skinType = response.data.analysis.resultYourSkinhealth.skinType,
            skinConditions = converters.skinConditionsToJson(
                response.data.analysis.resultYourSkinhealth.skinConditions
            ),
            recommendations = converters.recommendationsToJson(
                response.data.recommendations.notes
            ),
        )
        //viewModel.saveSkinAnalysis(skinAnalysis)
    }

    private fun displayResults(response: AnalyzeResponse) {
        with(binding) {
            // Display skin type
            tvSkinType.text = buildString {
                append("Skin Type: ")
                append(response.data.analysis.resultYourSkinhealth.skinType)
            }

            // Display skin conditions
            val conditions = response.data.analysis.resultYourSkinhealth.skinConditions
            tvSkinConditions.text = buildString {
                append("Skin Conditions:\n")
                append("• Acne: ${conditions.acne}\n")
                append("• Wrinkles: ${conditions.wrinkles}\n")
                append("• Redness: ${conditions.redness}\n")
            }

            // Display recommendations
            tvRecommendations.text = buildString {
                append("Recommendations:\n")
                response.data.recommendations.notes.forEach { note ->
                    append("• $note\n")
                }
            }

            setupProductsRecyclerView(response.data.recommendations.basicRoutine)
        }
    }

    private fun setupProductsRecyclerView(basicRoutine: BasicRoutine) {
        val productsAdapter = ProductAdapter()

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productsAdapter
        }

        // Combine all products into a single list
        val allProducts = mutableListOf<Any>().apply {
            addAll(basicRoutine.facialWash)
            addAll(basicRoutine.toner)
            addAll(basicRoutine.serum)
            addAll(basicRoutine.moisturizer)
            addAll(basicRoutine.sunscreen)
        }

        productsAdapter.submitList(allProducts as List<ProductEntity?>?)
    }

    private fun showError(message: String) {
        binding.apply {
            tvError.visibility = View.VISIBLE
            tvError.text = message
            groupResults.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

