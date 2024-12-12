package com.capstone.skinpal.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: Repository): ViewModel() {
    val products: LiveData<Result<List<ProductEntity>>> = repository.getProducts()
    fun searchProducts(query: String) = repository.searchProducts(query)

    fun getDetailProduct(query: String) = repository.getDetailProduct(query)
    fun saveEvent(product: ProductEntity) {
        viewModelScope.launch {
            repository.setBookmarkedProduct(product, true)
        }
    }

    fun getBookmarkedNews() = repository.getFavoriteProduct()
    fun deleteEvent(product: ProductEntity) {
        viewModelScope.launch {
            repository.setBookmarkedProduct(product, false)
        }
    }
    fun findFavoriteProducts() = repository.getFavoriteProduct()
}