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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }
}
