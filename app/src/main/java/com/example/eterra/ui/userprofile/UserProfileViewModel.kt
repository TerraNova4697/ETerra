package com.example.eterra.ui.userprofile

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _userProfileEvents = MutableSharedFlow<UserProfileEvents>()
    val userProfileEvents = _userProfileEvents.asSharedFlow()

    fun onBtnSubmitClicked(mobileNumber: String) = viewModelScope.launch{
        if (TextUtils.isEmpty(mobileNumber.trim {it <= ' '})) {
            _userProfileEvents.emit(UserProfileEvents.EnterMobileNumberError)
        } else {
            _userProfileEvents.emit(UserProfileEvents.ProfileCompleted)
        }
    }

    fun onIvUserPhotoClicked() = viewModelScope.launch {
        _userProfileEvents.emit(UserProfileEvents.PickImage)
    }

    sealed class UserProfileEvents {
        object EnterMobileNumberError: UserProfileEvents()
        object ProfileCompleted: UserProfileEvents()
        object PickImage: UserProfileEvents()
    }

}