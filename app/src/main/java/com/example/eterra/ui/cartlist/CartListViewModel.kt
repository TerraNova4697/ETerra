package com.example.eterra.ui.cartlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

    private fun collectCartItems() {
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
                                if (product.id == cartItem.product_id) {
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
                    _cartListUiEvents.emit(CartListUiEvent.ShowError(loadCartItemsResult.errorMessage))
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
                    _cartListUiEvents.emit(CartListUiEvent.ShowError(loadProductsResult.errorMessage))
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
                _cartListUiEvents.emit(CartListUiEvent.ShowError(removeItemResult.errorMessage))
            }
        }
    }

    fun onRemoveOneItemClicked(cartId: String, currentCartQuantity: String) = viewModelScope.launch {
        _cartListUiEvents.emit(CartListUiEvent.ShowProgressBar)
        if (currentCartQuantity == "0") {
            onCartItemDeleteClicked(cartId)
        } else {
            val cartQuantity = currentCartQuantity.toInt()
            val itemHashMap = HashMap<String, Any>()
            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()
            val cartUpdateResult = firestoreRepo.updateCartItem(cartId, itemHashMap)
            when (cartUpdateResult) {
                is FirestoreRepo.UpdateCartItemResult.Success -> {
                    collectProductItems()
                }
                is FirestoreRepo.UpdateCartItemResult.Failure -> {
                    _cartListUiEvents.emit(CartListUiEvent.HideProgressBar)
                    _cartListUiEvents.emit(CartListUiEvent.ShowError(cartUpdateResult.errorMessage))
                }
            }
        }
    }

    fun onAddOneItemClicked(cartId: String, currentCartQuantity: String, stockQuantity: String) = viewModelScope.launch {
        val cartQuantity = currentCartQuantity.toInt()
        if (cartQuantity < stockQuantity.toInt()) {
            val itemHashMap = HashMap<String, Any>()
            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()
            val cartUpdateResult = firestoreRepo.updateCartItem(cartId, itemHashMap)
            when (cartUpdateResult) {
                is FirestoreRepo.UpdateCartItemResult.Success -> {
                    collectProductItems()
                }
                is FirestoreRepo.UpdateCartItemResult.Failure -> {
                    _cartListUiEvents.emit(CartListUiEvent.HideProgressBar)
                    _cartListUiEvents.emit(CartListUiEvent.ShowError(cartUpdateResult.errorMessage))
                }
            }
        }
    }

    sealed class CartListUiEvent{
        object ShowProgressBar: CartListUiEvent()
        object HideProgressBar: CartListUiEvent()
        data class ShowError(val errorMessage: String): CartListUiEvent()
    }

}