package com.example.eterra.ui.productdetails

import android.util.Log
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
class ProductDetailsViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _productDetailsUiEvents = MutableSharedFlow<ProductDetailsUiEvent>()
    val productDetailsUiEvents = _productDetailsUiEvents.asSharedFlow()

    fun fetchProductDetails(productId: String) = viewModelScope.launch {
        _productDetailsUiEvents.emit(ProductDetailsUiEvent.ShowProgressBar)
        val fetchResult = firestoreRepo.getProductDetails(productId)
        when (fetchResult) {
            is FirestoreRepo.ProductDetails.Success -> {
                Log.i(this@ProductDetailsViewModel.javaClass.simpleName, fetchResult.product.toString())
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.DisplayDetails(fetchResult.product))
            }
            is FirestoreRepo.ProductDetails.Failure -> {
                Log.i(this@ProductDetailsViewModel.javaClass.simpleName, fetchResult.errorMessage)
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
            }
        }

    }

    sealed class ProductDetailsUiEvent {
        data class DisplayDetails(val product: Product): ProductDetailsUiEvent()
        object ShowProgressBar: ProductDetailsUiEvent()
        object HideProgressBar: ProductDetailsUiEvent()
    }

}