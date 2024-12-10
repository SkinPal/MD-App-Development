package com.capstone.skinpal.ui.product

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.FragmentFavoriteBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.ui.product.DetailActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: FavoriteAdapter
    private val favoriteViewModel: ProductViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //checkInternetConnection()

        productAdapter = FavoriteAdapter  { event ->
            navigateToDetailEvent(event)
        }

        binding.toolbar.title = "Favorite Products"
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_arrow
                )
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = productAdapter

        observeFavoriteEvents()

    }

    private fun observeFavoriteEvents() {
        favoriteViewModel.findFavoriteProducts().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, productAdapter)
        }
    }

    private fun handleEventResult(result: Result<List<ProductEntity>>, adapter: FavoriteAdapter) {
        when (result) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvNoEvent.visibility = View.GONE
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                val eventData = result.data
                if (eventData.isEmpty()) {
                    binding.tvNoEvent.visibility = View.VISIBLE
                    binding.rvEvent.visibility = View.GONE
                } else {
                    binding.tvNoEvent.visibility = View.GONE
                    binding.rvEvent.visibility = View.VISIBLE
                    adapter.submitList(eventData)
                }
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvNoEvent.visibility = View.VISIBLE
                binding.rvEvent.visibility = View.GONE
                Toast.makeText(
                    context,
                    result.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun navigateToDetailEvent(product: ProductEntity) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_PRODUCT_ID, product.name.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
