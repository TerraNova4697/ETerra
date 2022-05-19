package com.example.eterra.ui.addresslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Address
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _addressListUiEvents: MutableSharedFlow<AddressListUiEvent> = MutableSharedFlow()
    val addressListUiEvents = _addressListUiEvents.asSharedFlow()

    private val _addressList = MutableLiveData<List<Address>>()
    val addressList = _addressList

    init {
        collectAddresses()
    }

    fun collectAddresses()  {
        viewModelScope.launch {
            _addressListUiEvents.emit(AddressListUiEvent.ShowProgressbar)
            when (val loadAddressesResult = firestoreRepo.getAddressesList()) {
                is FirestoreRepo.GetAddressesListResult.Success -> {
                    _addressList.value = loadAddressesResult.cartList
                    _addressListUiEvents.emit(AddressListUiEvent.HideProgressbar)
                }
                is FirestoreRepo.GetAddressesListResult.Failure -> {
                    _addressListUiEvents.emit(AddressListUiEvent.HideProgressbar)
                    _addressListUiEvents.emit(AddressListUiEvent.ShowError(loadAddressesResult.errorMessage))
                }
            }
        }
    }

    sealed class AddressListUiEvent {
        object ShowProgressbar: AddressListUiEvent()
        object HideProgressbar: AddressListUiEvent()
        data class ShowError(val errorMessage: String): AddressListUiEvent()
    }

}