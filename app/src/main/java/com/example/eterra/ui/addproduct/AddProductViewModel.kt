package com.example.eterra.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(): ViewModel() {

    private val _addProductUiEvents: MutableSharedFlow<AddProductUiEvent> = MutableSharedFlow()
    val addProductUiEvents = _addProductUiEvents.asSharedFlow()

    fun onAddPhotoClicked() = viewModelScope.launch {
        _addProductUiEvents.emit(AddProductUiEvent.PickPhoto)
    }

    sealed class AddProductUiEvent {
        object PickPhoto: AddProductUiEvent()
    }
}