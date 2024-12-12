package com.capstone.skinpal.ui.product

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.ActivityDetailBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.capstone.skinpal.R
import com.capstone.skinpal.data.Result
import kotlin.getValue

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var isBookmarked = false
    private var product: ProductEntity? = null
    private val detailViewModel by viewModels<ProductViewModel> {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isConnectedToInternet()) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.ic_arrow
                )
            )
            title = getString(R.string.detail_product)
        }

        val eventId = intent.getStringExtra(EXTRA_PRODUCT_ID)

        if (eventId != null) {
            observeEventDetails(eventId)
        }

        binding.floatingActionButton.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun observeEventDetails(name: String) {
        detailViewModel.getDetailProduct(name).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val products = result.data
                    product = products
                    isBookmarked = products.isBookmarked == true
                    updateFabIcon(isBookmarked)
                    populateEventDetails(products)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    //Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateEventDetails(product: ProductEntity) {
        binding.productName.text = product.name
        binding.productDescription.text = product.description
        binding.productIngredients.text = product.ingredients
        Glide.with(this)
            .load(product.imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
            )
            .into(binding.imageView)
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        product?.let { event ->
            updateFabIcon(isBookmarked)
            if (isBookmarked) {
                detailViewModel.saveEvent(event)
                Toast.makeText(this, getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show()
            } else {
                detailViewModel.deleteEvent(event)
                Toast.makeText(this, getString(R.string.removed_from_favorite), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFabIcon(isBookmarked: Boolean) {
        val iconRes = if (isBookmarked) R.drawable.ic_favorite_fill else R.drawable.ic_favorite_border
        binding.floatingActionButton.setImageResource(iconRes)
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}
