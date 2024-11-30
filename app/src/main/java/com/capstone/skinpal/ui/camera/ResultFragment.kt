package com.capstone.skinpal.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.skinpal.databinding.BottomSheetResultBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetResultBinding? = null
    private val binding get() = _binding!!

    private var imageName: String? = null
    private var additionalInfo: String? = null

    companion object {
        fun newInstance(imageName: String, additionalInfo: String): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putString("IMAGE_NAME", imageName)
            args.putString("ADDITIONAL_INFO", additionalInfo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use ViewBinding to inflate the layout
        _binding = BottomSheetResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        imageName = arguments?.getString("IMAGE_NAME")
        additionalInfo = arguments?.getString("ADDITIONAL_INFO")

        // Set text to the TextViews using ViewBinding
        binding.imageNameTextView.text = imageName
        binding.additionalInfoTextView.text = additionalInfo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
