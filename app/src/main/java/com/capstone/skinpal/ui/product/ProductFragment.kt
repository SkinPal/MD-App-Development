package com.capstone.skinpal.ui.product

import com.capstone.skinpal.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.FragmentProductBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.home.HomeViewModel

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireActivity()))
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
        observeProducts()
        //productViewModel.products.observe(viewLifecycleOwner) { products ->
         //   productAdapter.submitList(products)
        //}
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter()
        binding.rvProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeProducts() {
        productViewModel.products.observe(viewLifecycleOwner) { result ->
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
                        productAdapter.submitList(product)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoItem.visibility = View.GONE
                    Log.e("ProductFragment", "Error loading products: ${result.error}")
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*private fun observeProduct() {
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
                        productAdapter.submitList(product)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoItem.visibility = View.GONE
                    Log.e("ProductFragment", "Error loading products")
                }
            }
        }

    }*/


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
