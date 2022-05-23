package com.example.eterra.ui.soldproducts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Order
import com.example.eterra.models.SoldProduct
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.ui.orders.OrdersViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoldProductsViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _soldProductsUiEvents: MutableSharedFlow<SoldProductsUiEvent> = MutableSharedFlow()
    val soldProductsUiEvent = _soldProductsUiEvents.asSharedFlow()

    private val _soldProducts = MutableLiveData<List<SoldProduct>>()
    val soldProducts = _soldProducts

    fun getSoldProductsList() = viewModelScope.launch{
        _soldProductsUiEvents.emit(SoldProductsUiEvent.ShowProgressbar)
        when (val getSoldProductsResult = firestoreRepo.getSoldProductsList()) {
            is FirestoreRepo.GetSoldProductsListResult.Success -> {
                _soldProductsUiEvents.emit(SoldProductsUiEvent.HideProgressbar)
                _soldProducts.value = getSoldProductsResult.ordersList
            }
            is FirestoreRepo.GetSoldProductsListResult.Failure -> {
                _soldProductsUiEvents.emit(SoldProductsUiEvent.HideProgressbar)
                _soldProductsUiEvents.emit(SoldProductsUiEvent.NotifyError(getSoldProductsResult.errorMessage))
            }
        }
    }

    sealed class SoldProductsUiEvent {
        object ShowProgressbar: SoldProductsUiEvent()
        object HideProgressbar: SoldProductsUiEvent()
        data class NotifyError(val errorMessage: String): SoldProductsUiEvent()
    }

}