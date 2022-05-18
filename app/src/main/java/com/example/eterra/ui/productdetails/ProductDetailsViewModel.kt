package com.example.eterra.ui.productdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.CartItem
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
) : ViewModel() {

    private val _productDetailsUiEvents = MutableSharedFlow<ProductDetailsUiEvent>()
    val productDetailsUiEvents = _productDetailsUiEvents.asSharedFlow()

    fun fetchProductDetails(productId: String) = viewModelScope.launch {
        _productDetailsUiEvents.emit(ProductDetailsUiEvent.ShowProgressBar)
        val fetchResult = firestoreRepo.getProductDetails(productId)
        when (fetchResult) {
            is FirestoreRepo.ProductDetails.Success -> {
                Log.i(
                    this@ProductDetailsViewModel.javaClass.simpleName,
                    fetchResult.product.toString()
                )
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.DisplayDetails(fetchResult.product))
            }
            is FirestoreRepo.ProductDetails.Failure -> {
                Log.i(this@ProductDetailsViewModel.javaClass.simpleName, fetchResult.errorMessage)
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
            }
        }

    }

    fun onAddToCartClicked(productDetails: Product, productId: String) = viewModelScope.launch{
        val cartItem = CartItem(
            user_id = firestoreRepo.getCurrentUserId(),
            product_id = productId,
            title = productDetails.title,
            price = productDetails.price,
            image = productDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        _productDetailsUiEvents.emit(ProductDetailsUiEvent.ShowProgressBar)
        val addToCartResult = firestoreRepo.addCartItems(cartItem)
        when (addToCartResult) {
            is FirestoreRepo.AddToCart.Success -> {
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.AddToCartSuccess)
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
            }
            is FirestoreRepo.AddToCart.Failure -> {
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.AddToCartFailure(addToCartResult.errorMessage))
                _productDetailsUiEvents.emit(ProductDetailsUiEvent.HideProgressBar)
            }
        }

    }

    sealed class ProductDetailsUiEvent {
        data class DisplayDetails(val product: Product) : ProductDetailsUiEvent()
        object ShowProgressBar : ProductDetailsUiEvent()
        object HideProgressBar : ProductDetailsUiEvent()
        object AddToCartSuccess: ProductDetailsUiEvent()
        data class AddToCartFailure(val errorMessage: String): ProductDetailsUiEvent()
    }

}