package com.capstone.skinpal.ui.product

import android.content.Intent
import android.graphics.Color
import com.capstone.skinpal.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.FragmentProductBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.BaseFragment
import com.capstone.skinpal.ui.ViewModelFactory

class ProductFragment : Fragment(), BaseFragment {

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

        binding.toolbar.title = "Product Recommendations"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        val bookmarkButton = binding.bookmarkButton
        bookmarkButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_product_to_navigation_favorite)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProducts()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { navigateToDetailProduct(it) }
        binding.rvProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun navigateToDetailProduct(product: ProductEntity) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_PRODUCT_ID, product.name.toString())
        }
        startActivity(intent)
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            visibility = View.VISIBLE
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    observeProducts(query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    observeProducts(query.orEmpty())
                    return true
                }
            })
        }
    }

    private fun observeProducts(query: String) {
        productViewModel.searchProducts(query).observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<List<ProductEntity>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                updateProductList(result.data)
            }
            is Result.Error -> {
                showError()
                handleApiError(result.error, requireContext())
            }
        }
    }

    private fun updateProductList(productData: List<ProductEntity>) {
        if (productData.isEmpty()) {
            binding.rvProduct.visibility = View.GONE
        } else {
            binding.tvNoItem.visibility = View.GONE
            binding.rvProduct.visibility = View.VISIBLE
            productAdapter.submitList(productData)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvNoItem.visibility = if (isLoading) View.GONE else binding.tvNoItem.visibility
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.rvProduct.visibility = View.GONE
        binding.tvNoItem.visibility = View.VISIBLE
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
                    productAdapter.submitList(product)
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

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}
