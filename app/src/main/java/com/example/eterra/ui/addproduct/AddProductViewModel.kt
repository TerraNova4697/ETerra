package com.example.eterra.ui.addproduct

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.repository.FirebaseStorageClass
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.ui.userprofile.UserProfileViewModel
import com.example.eterra.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val firebaseStorageClass: FirebaseStorageClass
): ViewModel() {

    private val _addProductUiEvents: MutableSharedFlow<AddProductUiEvent> = MutableSharedFlow()
    val addProductUiEvents = _addProductUiEvents.asSharedFlow()

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
        val uploadResult = firebaseStorageClass.uploadProfileImage(imageUrl!!, imageExtension, Constants.PRODUCT_IMAGE)
        when (uploadResult) {
            is FirebaseStorageClass.UploadProfileImage.Success -> {
//                val userHashMap = HashMap<String, Any>()
//                userHashMap[Constants.IMAGE] = uploadResult.imageUri.toString()
//                val updateResult = firestoreRepo.updateUserDetails(userHashMap)
//                when (updateResult) {
//                    is FirestoreRepo.UpdateUserDetails.Success -> {
//                        _userProfileEvents.emit(UserProfileViewModel.UserProfileEvents.HideProgressDialog)
//                    }
//                    is FirestoreRepo.UpdateUserDetails.Failure -> {
//                        _userProfileEvents.emit(UserProfileViewModel.UserProfileEvents.ErrorWhileUpdating(updateResult.message))
//                    }
//                }
//                _userProfileEvents.emit(UserProfileViewModel.UserProfileEvents.PlaceImage(uploadResult.imageUri))
            }
            is FirebaseStorageClass.UploadProfileImage.Failure -> {
//                _userProfileEvents.emit(UserProfileViewModel.UserProfileEvents.ErrorWhileUpdating(uploadResult.message))
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
    }
}