package com.capstone.skinpal.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.data.remote.response.ProductResponse
import com.capstone.skinpal.data.remote.response.ProductResponseItem
import com.capstone.skinpal.ui.Repository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: Repository): ViewModel() {
    val products: LiveData<Result<List<ProductEntity>>> = repository.getProducts()
    fun searchProducts(query: String) = repository.searchProducts(query)
}