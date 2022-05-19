package com.example.eterra.ui.addeditaddress

import android.text.TextUtils
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
class AddEditAddressViewModel @Inject constructor(
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _addEditAddressUiEvents: MutableSharedFlow<AddEditAddressUiEvent> = MutableSharedFlow()
    val addEditAddressUiEvents = _addEditAddressUiEvents.asSharedFlow()

    fun onBtnSubmitClicked(name: String, number: String, address: String, zipCode: String, type: String, additionalInfo: String) = viewModelScope.launch{
        _addEditAddressUiEvents.emit(AddEditAddressUiEvent.ShowProgressBar)
        when {
            TextUtils.isEmpty(name.trim { it <= ' ' }) -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.NameErrorEvent)
                return@launch
            }
            TextUtils.isEmpty(number.trim { it <= ' ' }) -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.NumberErrorEvent)
                return@launch
            }
            TextUtils.isEmpty(address.trim { it <= ' ' }) -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.AddressErrorEvent)
                return@launch
            }
            TextUtils.isEmpty(zipCode.trim { it <= ' ' }) -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.ZipCodeErrorEvent)
                return@launch
            }
            TextUtils.isEmpty(address.trim { it <= ' ' }) -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.AddressErrorEvent)
                return@launch
            }
        }

        val addressModel = Address(
            user_id = firestoreRepo.getCurrentUserId(),
            name = name,
            mobileNumber = number,
            address = address,
            zipCode = zipCode,
            type = type,
            additionalNote = additionalInfo
        )

        when (val addEditAddressResult = firestoreRepo.addAddress(addressModel)) {
            is FirestoreRepo.AddToAddresses.Success -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.HideProgressBar)
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.NavigateUp)
            }
            is FirestoreRepo.AddToAddresses.Failure -> {
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.HideProgressBar)
                _addEditAddressUiEvents.emit(AddEditAddressUiEvent.ShowErrorSnackBar(addEditAddressResult.errorMessage))
            }
        }
    }

    sealed class AddEditAddressUiEvent {
        object ShowProgressBar: AddEditAddressUiEvent()
        object HideProgressBar: AddEditAddressUiEvent()
        object NameErrorEvent: AddEditAddressUiEvent()
        object NumberErrorEvent : AddEditAddressUiEvent()
        object AddressErrorEvent: AddEditAddressUiEvent()
        object ZipCodeErrorEvent: AddEditAddressUiEvent()
        object NavigateUp: AddEditAddressUiEvent()
        data class ShowErrorSnackBar(val errorMessage: String): AddEditAddressUiEvent()
    }

}