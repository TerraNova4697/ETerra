package com.example.eterra.ui.checkout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.CartItem
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _checkoutViewModelEvents: MutableSharedFlow<CheckoutUiEvent> = MutableSharedFlow()
    val checkoutViewModelEvents = _checkoutViewModelEvents.asSharedFlow()

    private val _products = MutableLiveData<List<Product>>()
    val products = _products

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems = _cartItems

    private fun collectCartItems() {
        viewModelScope.launch {
            val loadCartItemsResult = firestoreRepo.getCartList()
            when (loadCartItemsResult) {
                is FirestoreRepo.GetCartList.Success -> {
                    _cartItems.value = loadCartItemsResult.cartList
                    for (product in _products.value!!) {
                        for (cartItem in _cartItems.value!!) {
                            if (product.id == cartItem.product_id) {
                                cartItem.stock_quantity = product.quantity

                                if (product.quantity.toInt() == 0) {
                                    cartItem.cart_quantity = product.quantity
                                }
                            }
                        }
                    }
                }
                is FirestoreRepo.GetCartList.Failure -> {
                    _checkoutViewModelEvents.emit(CheckoutUiEvent.ErrorFetchingProducts(loadCartItemsResult.errorMessage))
                }
            }
            _checkoutViewModelEvents.emit(CheckoutUiEvent.HideProgressBar)
        }
    }

    fun collectProducts() {
        viewModelScope.launch {
            _checkoutViewModelEvents.emit(CheckoutUiEvent.ShowProgressBar)
            val loadProductsResult = firestoreRepo.getProductsList()
            when (loadProductsResult) {
                is FirestoreRepo.LoadUsersProductsList.Success -> {
                    _products.value = loadProductsResult.products
                    collectCartItems()
                }
                is FirestoreRepo.LoadUsersProductsList.Failure -> {
                    _checkoutViewModelEvents.emit(CheckoutUiEvent.ErrorFetchingProducts(loadProductsResult.errorMessage))
                    Log.i(this@CheckoutViewModel.javaClass.simpleName, loadProductsResult.errorMessage)
                }
            }
            _checkoutViewModelEvents.emit(CheckoutUiEvent.HideProgressBar)
        }
    }

    sealed class CheckoutUiEvent {
        object ShowProgressBar: CheckoutUiEvent()
        object HideProgressBar: CheckoutUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): CheckoutUiEvent()
        object ErrorWhileDeleting: CheckoutUiEvent()
    }

}