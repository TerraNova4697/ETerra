package com.example.eterra.ui.cartlist

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

    private val _products = MutableLiveData<List<Product>>()
    val products = _products

    init {
        collectCartItems()
    }

    fun collectCartItems() {
        viewModelScope.launch {
            _cartListUiEvents.emit(CartListUiEvent.ShowProgressBar)
            val loadCartItemsResult = firestoreRepo.getCartList()
            when (loadCartItemsResult) {
                is FirestoreRepo.GetCartList.Success -> {
                    _cartItems.value = loadCartItemsResult.cartList
                    calculateSubtotal(loadCartItemsResult.cartList)
                    if (_products.value != null && _products.value!!.isNotEmpty()
                        && _cartItems.value != null && _cartItems.value!!.isNotEmpty()) {

                        for (product in _products.value!!) {
                            for (cartItem in _cartItems.value!!) {
                                if (product.id == cartItem.id) {
                                    cartItem.stock_quantity = product.quantity

                                    if (product.quantity.toInt() == 0) {
                                        cartItem.cart_quantity = product.quantity
                                    }
                                }
                            }
                        }
                    }
                }
                is FirestoreRepo.GetCartList.Failure -> {
                    _cartListUiEvents.emit(CartListUiEvent.ErrorFetchingProducts(loadCartItemsResult.errorMessage))
                }
            }
            _cartListUiEvents.emit(CartListUiEvent.HideProgressBar)
        }
    }

    fun collectProductItems() {
        viewModelScope.launch {
            _cartListUiEvents.emit(CartListUiEvent.ShowProgressBar)
            val loadProductsResult = firestoreRepo.getAllProductsList()
            when (loadProductsResult) {
                is FirestoreRepo.GetAllProductsResult.Success -> {
                    _products.value = loadProductsResult.list
                    collectCartItems()
                }
                is FirestoreRepo.GetAllProductsResult.Failure -> {
                    _cartListUiEvents.emit(CartListUiEvent.ErrorFetchingProducts(loadProductsResult.errorMessage))
                }
            }
//            _cartListUiEvents.emit(CartListUiEvent.HideProgressBar)
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

    fun onCartItemDeleteClicked(cartId: String) = viewModelScope.launch {
        val removeItemResult = firestoreRepo.removeItemFromCart(cartId)
        when (removeItemResult) {
            is FirestoreRepo.RemoveCartResult.Success -> {
                collectProductItems()
            }
            is FirestoreRepo.RemoveCartResult.Failure -> {
                collectProductItems()
                _cartListUiEvents.emit(CartListUiEvent.ErrorRemovingCartItem(removeItemResult.errorMessage))
            }
        }
    }

    sealed class CartListUiEvent{
        object ShowProgressBar: CartListUiEvent()
        object HideProgressBar: CartListUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): CartListUiEvent()
        data class ErrorRemovingCartItem(val errorMessage: String): CartListUiEvent()
    }

}