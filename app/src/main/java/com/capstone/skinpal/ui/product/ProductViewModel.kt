package com.capstone.skinpal.ui.product

import androidx.lifecycle.ViewModel
import com.capstone.skinpal.ui.Repository

class ProductViewModel(private val repository: Repository): ViewModel() {
    fun getProduct() = repository.getProduct()
}