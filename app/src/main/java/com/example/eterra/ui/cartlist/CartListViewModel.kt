package com.example.eterra.ui.cartlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.CartItem
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.ui.products.ProductsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class CartListViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _cartListUiEvents = MutableSharedFlow<CartListUiEvent>()
    val cartListUiEvents = _cartListUiEvents.asSharedFlow()

    private val _subtotal = MutableLiveData<Double>(0.0)
    val subtotal = _subtotal

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems = _cartItems

    init {
        collectCartItems()
    }

    fun collectCartItems() {
        viewModelScope.launch {
            _cartListUiEvents.emit(CartListUiEvent.ShowProgressBar)
            val loadProductsResult = firestoreRepo.getCartList()
            when (loadProductsResult) {
                is FirestoreRepo.GetCartList.Success -> {
                    _cartItems.value = loadProductsResult.cartList
                    calculateSubtotal(loadProductsResult.cartList)
                }
                is FirestoreRepo.GetCartList.Failure -> {
                    _cartListUiEvents.emit(CartListUiEvent.ErrorFetchingProducts(loadProductsResult.errorMessage))
                }
            }
            _cartListUiEvents.emit(CartListUiEvent.HideProgressBar)
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

    sealed class CartListUiEvent{
        object ShowProgressBar: CartListUiEvent()
        object HideProgressBar: CartListUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): CartListUiEvent()
    }

}