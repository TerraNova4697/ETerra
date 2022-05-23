package com.example.eterra.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Order
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _ordersUiEvents: MutableSharedFlow<OrdersUiEvent> = MutableSharedFlow()
    val ordersUiEvent = _ordersUiEvents.asSharedFlow()

    private val _orders = MutableLiveData<List<Order>>()
    val orders = _orders

    fun getOrdersList() = viewModelScope.launch{
        _ordersUiEvents.emit(OrdersUiEvent.ShowProgressbar)
        when (val getOrdersResult = firestoreRepo.getOrdersList()) {
            is FirestoreRepo.GetOrdersListResult.Success -> {
                _ordersUiEvents.emit(OrdersUiEvent.HideProgressbar)
                _orders.value = getOrdersResult.ordersList
            }
            is FirestoreRepo.GetOrdersListResult.Failure -> {
                _ordersUiEvents.emit(OrdersUiEvent.HideProgressbar)
                _ordersUiEvents.emit(OrdersUiEvent.NotifyError(getOrdersResult.errorMessage))
            }
        }
    }

    sealed class OrdersUiEvent {
        object ShowProgressbar: OrdersUiEvent()
        object HideProgressbar: OrdersUiEvent()
        data class NotifyError(val errorMessage: String): OrdersUiEvent()
    }

}