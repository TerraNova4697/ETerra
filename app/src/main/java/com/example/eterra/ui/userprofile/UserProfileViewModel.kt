package com.example.eterra.ui.userprofile

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firestoreRepo: FirestoreRepo
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

    sealed class UserProfileEvents {
        object EnterMobileNumberError: UserProfileEvents()
        object ProfileCompleted: UserProfileEvents()
        object PickImage: UserProfileEvents()
        object ShowProgressDialog: UserProfileEvents()
        object NavigateBack: UserProfileEvents()
        data class ErrorWhileUpdating(val message: String): UserProfileEvents()
    }

}