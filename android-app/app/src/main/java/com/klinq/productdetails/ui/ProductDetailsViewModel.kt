package com.klinq.productdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klinq.productdetails.data.Product
import com.klinq.productdetails.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data class Success(val product: Product) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class ProductDetailsViewModel(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun retry() {
        loadProduct()
    }

    private fun loadProduct() {
        _uiState.value = ProductUiState.Loading
        viewModelScope.launch {
            _uiState.value = runCatching {
                ProductUiState.Success(repository.getProduct())
            }.getOrElse {
                ProductUiState.Error(
                    it.message ?: "Unable to load this product. Please try again.",
                )
            }
        }
    }
}

class ProductDetailsViewModelFactory(
    private val repository: ProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            return ProductDetailsViewModel(repository) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}