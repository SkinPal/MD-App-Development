package com.capstone.skinpal.ui.product

import com.capstone.skinpal.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.FragmentProductBinding
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.home.HomeViewModel

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProduct()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.rvProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeProduct() {
        productViewModel.getProduct().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvNoItem.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val product = result.data
                    if (product.isEmpty()) {
                        binding.tvNoItem.visibility = View.VISIBLE
                    } else {
                        binding.tvNoItem.visibility = View.GONE
                        productAdapter.submitList(product as List<ProductEntity?>?)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoItem.visibility = View.GONE
                }
            }
        }
    }

    fun navigateToHistoryFragment() {
        findNavController().navigate(
            R.id.navigation_reminder,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
