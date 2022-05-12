package com.example.eterra.ui.userprofile

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.repository.FirebaseStorageClass
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.utils.Constants
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firestoreRepo: FirestoreRepo,
    private val firebaseStorageClass: FirebaseStorageClass
): ViewModel() {

    private val _userProfileEvents = MutableSharedFlow<UserProfileEvents>()
    val userProfileEvents = _userProfileEvents.asSharedFlow()

    fun onBtnSubmitClicked(mobileNumber: String, gender: String) = viewModelScope.launch{
        if (TextUtils.isEmpty(mobileNumber.trim {it <= ' '})) {
            _userProfileEvents.emit(UserProfileEvents.EnterMobileNumberError)
        } else {
            _userProfileEvents.emit(UserProfileEvents.ShowProgressDialog)
            val userHashMap = HashMap<String, Any>()
            userHashMap[Constants.MOBILE] = mobileNumber.trim {it <= ' '}.toLong()
            userHashMap[Constants.GENDER] = gender
            userHashMap[Constants.PROFILE_COMPLETED] = 1
            val updateResult = firestoreRepo.updateUserDetails(userHashMap)
            when (updateResult) {
                is FirestoreRepo.UpdateUserDetails.Success -> {
                    _userProfileEvents.emit(UserProfileEvents.NavigateBack)
                }
                is FirestoreRepo.UpdateUserDetails.Failure -> {
                    _userProfileEvents.emit(UserProfileEvents.ErrorWhileUpdating(updateResult.message))
                }
            }
        }
    }

    fun onIvUserPhotoClicked() = viewModelScope.launch {
        _userProfileEvents.emit(UserProfileEvents.PickImage)
    }

    fun onImagePicked(selectedImageUri: Uri, imageExtension: String) = viewModelScope.launch {
        _userProfileEvents.emit(UserProfileEvents.ShowProgressDialog)
        val uploadResult = firebaseStorageClass.uploadProfileImage(selectedImageUri, imageExtension, Constants.USER_PROFILE_IMAGE)
        when (uploadResult) {
            is FirebaseStorageClass.UploadProfileImage.Success -> {
                val userHashMap = HashMap<String, Any>()
                userHashMap[Constants.IMAGE] = uploadResult.imageUri.toString()
                val updateResult = firestoreRepo.updateUserDetails(userHashMap)
                when (updateResult) {
                    is FirestoreRepo.UpdateUserDetails.Success -> {
                        _userProfileEvents.emit(UserProfileEvents.HideProgressDialog)
                    }
                    is FirestoreRepo.UpdateUserDetails.Failure -> {
                        _userProfileEvents.emit(UserProfileEvents.ErrorWhileUpdating(updateResult.message))
                    }
                }
                _userProfileEvents.emit(UserProfileEvents.PlaceImage(uploadResult.imageUri))
            }
            is FirebaseStorageClass.UploadProfileImage.Failure -> {
                _userProfileEvents.emit(UserProfileEvents.ErrorWhileUpdating(uploadResult.message))
            }
        }
    }

    sealed class UserProfileEvents {
        object EnterMobileNumberError: UserProfileEvents()
        object ProfileCompleted: UserProfileEvents()
        object PickImage: UserProfileEvents()
        object ShowProgressDialog: UserProfileEvents()
        object HideProgressDialog: UserProfileEvents()
        object NavigateBack: UserProfileEvents()
        data class ErrorWhileUpdating(val message: String): UserProfileEvents()
        data class PlaceImage(val imageUri: Uri): UserProfileEvents()
    }

}