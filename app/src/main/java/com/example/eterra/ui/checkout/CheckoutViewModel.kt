package com.example.eterra.ui.checkout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Address
import com.example.eterra.models.CartItem
import com.example.eterra.models.Order
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
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

    private val _subtotal = MutableLiveData(0.0)
    val subtotal = _subtotal

    private val _shippingCharge = MutableLiveData(10.0)
    val shippingCharge = _shippingCharge

    private val _total = MutableLiveData(0.0)
    val total = _total

    private fun collectCartItems() {
        viewModelScope.launch {
            when (val loadCartItemsResult = firestoreRepo.getCartList()) {
                is FirestoreRepo.GetCartList.Success -> {
                    _cartItems.value = loadCartItemsResult.cartList
                    calculateSubtotal(loadCartItemsResult.cartList)
                    calculateTotal()
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
            when (val loadProductsResult = firestoreRepo.getProductsList()) {
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

    private fun calculateSubtotal(cartList: ArrayList<CartItem>) {
        var subtotal = 0.0
        for (item in cartList) {
            val price: Double = item.price.toDouble()
            val quantity = item.cart_quantity.toInt()
            subtotal += (price * quantity)
        }
        _subtotal.value = subtotal
    }

    private fun calculateTotal() {
        _total.value = _subtotal.value?.plus(_shippingCharge.value!!)
    }

    fun onPlaceOrderClicked(address: Address) = viewModelScope.launch{
        _checkoutViewModelEvents.emit(CheckoutUiEvent.ShowProgressBar)
        val order = Order(
            firestoreRepo.getCurrentUserId(),
            ArrayList(cartItems.value!!),
            address,
            "Order${System.currentTimeMillis()}",
            cartItems.value!![0].image,
            subtotal.value.toString(),
            shippingCharge.value.toString(),
            total.value.toString()
        )
        when(val placeOrderResult = firestoreRepo.placeOrder(order)) {
            is FirestoreRepo.PlaceOrderResult.Success -> {
                _checkoutViewModelEvents.emit(CheckoutUiEvent.HideProgressBar)
                _checkoutViewModelEvents.emit(CheckoutUiEvent.NotifyUser("Your order was placed successfully."))
                _checkoutViewModelEvents.emit(CheckoutUiEvent.NavigateToDashboard)
            }
            is FirestoreRepo.PlaceOrderResult.Failure -> {
                _checkoutViewModelEvents.emit(CheckoutUiEvent.ErrorFetchingProducts(placeOrderResult.errorMessage))
                _checkoutViewModelEvents.emit(CheckoutUiEvent.HideProgressBar)
            }
        }


    }

    sealed class CheckoutUiEvent {
        object ShowProgressBar: CheckoutUiEvent()
        object HideProgressBar: CheckoutUiEvent()
        data class NotifyUser(val message: String): CheckoutUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): CheckoutUiEvent()
        object NavigateToDashboard: CheckoutUiEvent()
    }

}