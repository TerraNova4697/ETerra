package com.example.eterra.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Product
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.ui.products.ProductsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _dashboardViewModelEvents: MutableSharedFlow<DashboardUiEvent> = MutableSharedFlow()
    val dashboardViewModelEvents = _dashboardViewModelEvents.asSharedFlow()

    private val _products = MutableLiveData<List<Product>>()
    val products = _products

    init {
        collectProducts()
    }

    sealed class DashboardUiEvent {
        object ShowProgressBar: DashboardUiEvent()
        object HideProgressBar: DashboardUiEvent()
        data class ErrorFetchingProducts(val errorMessage: String): DashboardUiEvent()
    }

    fun collectProducts() {
        viewModelScope.launch {
            _dashboardViewModelEvents.emit(DashboardUiEvent.ShowProgressBar)
            val loadProductsResult = firestoreRepo.getProductsList()
            when (loadProductsResult) {
                is FirestoreRepo.LoadUsersProductsList.Success -> {
                    _products.value = loadProductsResult.products
                    Log.i(this@DashboardViewModel.javaClass.simpleName, loadProductsResult.products.toString())
                }
                is FirestoreRepo.LoadUsersProductsList.Failure -> {
                    _dashboardViewModelEvents.emit(DashboardUiEvent.ErrorFetchingProducts(loadProductsResult.errorMessage))
                    Log.i(this@DashboardViewModel.javaClass.simpleName, loadProductsResult.errorMessage)
                }
            }
            _dashboardViewModelEvents.emit(DashboardUiEvent.HideProgressBar)
        }
    }

}