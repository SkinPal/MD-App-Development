package com.capstone.skinpal.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.data.remote.response.AnalyzeResponse
import com.capstone.skinpal.databinding.BottomSheetResultBinding
import com.capstone.skinpal.ui.product.ProductAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.history.CameraWeeklyViewModel
import com.capstone.skinpal.ui.history.ResultAdapter
import java.io.File

class ResultFragment : BottomSheetDialogFragment() {

    private lateinit var progressTextView: TextView
    private lateinit var skinTypeTextView: TextView
    private lateinit var skinConditionsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesTextView: TextView
    private lateinit var adapter: ResultAdapter

    // Use activityViewModels instead of viewModels for fragments
    private val cameraWeeklyViewModel by activityViewModels<CameraWeeklyViewModel> {
        ViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind UI elements
        progressTextView = view.findViewById(R.id.progress_message)
        skinTypeTextView = view.findViewById(R.id.skin_type)
        skinConditionsTextView = view.findViewById(R.id.skin_conditions)
        recyclerView = view.findViewById(R.id.facial_wash_recycler)
        notesTextView = view.findViewById(R.id.notes)

        // Initialize the RecyclerView adapter
        adapter = ResultAdapter()

        // Observe the result of analyzeImage API
        val imageFile = File("/path/to/your/image.jpg") // Replace with actual file path
        val userId = "user_id" // Replace with actual user ID
        val week = "week" // Replace with actual week value

        cameraWeeklyViewModel.analyzeImage(imageFile = imageFile, user_id = userId, week = week)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        // Show loading state if needed
                        // You can display a loading indicator here
                    }
                    is Result.Success -> {
                        val analysisData = result.data

                        // Update UI with analysis result
                        val skinHealth = analysisData.resultYourSkinhealth
                        skinTypeTextView.text = "Skin Type: ${skinHealth.skinType}"
                        skinConditionsTextView.text = "Conditions: Acne: ${skinHealth.skinConditions.acne}% Redness: ${skinHealth.skinConditions.redness}% Wrinkles: ${skinHealth.skinConditions.wrinkles}%"

                        progressTextView.text = "${analysisData.progress.percentage}% progress"

                        // Set the facial wash products in RecyclerView
                        adapter.submitList(analysisData.recommendations.moisturizer)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.adapter = adapter

                        // Set notes
                        notesTextView.text = analysisData.notes.joinToString("\n")
                    }
                    is Result.Error -> {
                        // Handle error
                        //Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
