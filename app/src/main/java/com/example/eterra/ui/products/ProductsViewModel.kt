package com.example.eterra.ui.products

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(): ViewModel() {

    private val _productsViewModelEvents: MutableSharedFlow<ProductsUiEvent> = MutableSharedFlow()
    val productsViewModelEvents = _productsViewModelEvents.asSharedFlow()

    sealed class ProductsUiEvent {

    }

}