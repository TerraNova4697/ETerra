package com.example.eterra.ui.products

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _productsViewModelEvents: MutableSharedFlow<ProductsUiEvent> = MutableSharedFlow()
    val productsViewModelEvents = _productsViewModelEvents.asSharedFlow()

    private val _products = MutableLiveData<List<Product>>()
    val products = _products

    init {
        collectProducts()
    }

    sealed class ProductsUiEvent {
        object ShowProgressBar: ProductsUiEvent()
        object HideProgressBar: ProductsUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): ProductsUiEvent()
        object ErrorWhileDeleting: ProductsUiEvent()
    }

    fun collectProducts() {
        viewModelScope.launch {
            _productsViewModelEvents.emit(ProductsUiEvent.ShowProgressBar)
            val loadProductsResult = firestoreRepo.getUsersProductsList()
            when (loadProductsResult) {
                is FirestoreRepo.LoadUsersProductsList.Success -> {
                    _products.value = loadProductsResult.products
                    Log.i(this@ProductsViewModel.javaClass.simpleName, loadProductsResult.products.toString())
                }
                is FirestoreRepo.LoadUsersProductsList.Failure -> {
                    _productsViewModelEvents.emit(ProductsUiEvent.ErrorFetchingProducts(loadProductsResult.errorMessage))
                    Log.i(this@ProductsViewModel.javaClass.simpleName, loadProductsResult.errorMessage)
                }
            }
            _productsViewModelEvents.emit(ProductsUiEvent.HideProgressBar)
        }
    }

    fun onProductDeleteClicked(productId: String) = viewModelScope.launch {
        _productsViewModelEvents.emit(ProductsUiEvent.ShowProgressBar)
        when (firestoreRepo.deleteProduct(productId)) {
            is FirestoreRepo.DeleteResult.Failure -> {
                _productsViewModelEvents.emit(ProductsUiEvent.ErrorWhileDeleting)
                _productsViewModelEvents.emit(ProductsUiEvent.HideProgressBar)
            }
            is FirestoreRepo.DeleteResult.Success -> {
                _productsViewModelEvents.emit(ProductsUiEvent.HideProgressBar)
                collectProducts()
            }
        }
    }

}