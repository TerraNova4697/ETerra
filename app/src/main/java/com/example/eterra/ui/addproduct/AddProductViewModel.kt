package com.example.eterra.ui.addproduct

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.Product
import com.example.eterra.repository.FirebaseStorageClass
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.repository.PreferencesManager
import com.example.eterra.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val firebaseStorageClass: FirebaseStorageClass,
    private val preferencesManager: PreferencesManager,
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    private val _addProductUiEvents: MutableSharedFlow<AddProductUiEvent> = MutableSharedFlow()
    val addProductUiEvents = _addProductUiEvents.asSharedFlow()
    private val userFlow = preferencesManager.userPreferencesFlow

    fun onAddPhotoClicked() = viewModelScope.launch {
        _addProductUiEvents.emit(AddProductUiEvent.PickPhoto)
    }

    fun onSubmitClicked(imageUrl: Uri?, imageExtension: String, title: String, price: String, description: String, quantity: String) = viewModelScope.launch {
        _addProductUiEvents.emit(AddProductUiEvent.ShowProgressBar)
        when {
            imageUrl == null -> {
                _addProductUiEvents.emit(AddProductUiEvent.ProvideImage)
                return@launch
            }
            TextUtils.isEmpty(title.trim { it <= ' ' }) -> {
                _addProductUiEvents.emit(AddProductUiEvent.ProvideTitle)
                return@launch
            }
            TextUtils.isEmpty(price.trim { it <= ' ' }) -> {
                _addProductUiEvents.emit(AddProductUiEvent.ProvidePrice)
                return@launch
            }
            TextUtils.isEmpty(description.trim { it <= ' ' }) -> {
                _addProductUiEvents.emit(AddProductUiEvent.ProvideDescription)
                return@launch
            }
            TextUtils.isEmpty(quantity.trim { it <= ' ' }) -> {
                _addProductUiEvents.emit(AddProductUiEvent.ProvideQuantity)
                return@launch
            }
        }
        val imageUploadResult = firebaseStorageClass.uploadProfileImage(imageUrl!!, imageExtension, Constants.PRODUCT_IMAGE)
        when (imageUploadResult) {
            is FirebaseStorageClass.UploadProfileImage.Success -> {
                val product = Product(
                    user_id = firestoreRepo.getCurrentUserId(),
                    user_name = "${userFlow.first().firstName} ${userFlow.first().lastName}",
                    title = title.trim { it <= ' ' },
                    price = price.trim { it <= ' ' },
                    description = description.trim { it <= ' ' },
                    quantity = quantity.trim { it <= ' ' },
                    image = imageUploadResult.imageUri.toString()
                )
                val productUpdateResult = firestoreRepo.uploadProductDetails(product)
                when (productUpdateResult) {
                    is FirestoreRepo.UploadProduct.Success -> {
                        _addProductUiEvents.emit(AddProductUiEvent.NavigateBack)
                    }
                    is FirestoreRepo.UploadProduct.Failure -> {
                        _addProductUiEvents.emit(AddProductUiEvent.NotifyUploadFailure(productUpdateResult.message))
                    }
                }
            }
            is FirebaseStorageClass.UploadProfileImage.Failure -> {
                _addProductUiEvents.emit(AddProductUiEvent.NotifyUploadFailure(imageUploadResult.message))
            }
        }
    }

    sealed class AddProductUiEvent {
        object ShowProgressBar: AddProductUiEvent()
        object PickPhoto: AddProductUiEvent()
        object ProvideImage: AddProductUiEvent()
        object ProvideTitle: AddProductUiEvent()
        object ProvidePrice: AddProductUiEvent()
        object ProvideDescription: AddProductUiEvent()
        object ProvideQuantity: AddProductUiEvent()
        object NavigateBack: AddProductUiEvent()
        data class NotifyUploadFailure(val errorMessage: String): AddProductUiEvent()
    }
}