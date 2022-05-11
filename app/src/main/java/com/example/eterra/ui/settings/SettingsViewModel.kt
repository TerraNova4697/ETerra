package com.example.eterra.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.User
import com.example.eterra.repository.FirebaseAuthClass
import com.example.eterra.repository.FirebaseStorageClass
import com.example.eterra.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseStorageClass: FirebaseStorageClass,
    private val preferencesManager: PreferencesManager,
    private val firebaseAuthClass: FirebaseAuthClass
): ViewModel() {

    private val userFlow = preferencesManager.userPreferencesFlow
    private val _settingsUiEvents: MutableSharedFlow<SettingsUiEvent> = MutableSharedFlow()
    val settingsUiEvent = _settingsUiEvents.asSharedFlow()

//    init {
//        viewModelScope.launch {
//            Log.i("MyTag", "name : ${userFlow.first().name}")
//            Log.i("MyTag", "gender : ${userFlow.first().gender}")
//            Log.i("MyTag", "email : ${userFlow.first().email}")
//            Log.i("MyTag", "mob : ${userFlow.first().mobileNumber}")
//        }
//    }

    fun onGetUserDetails() = viewModelScope.launch{
        val firstName = userFlow.first().firstName
        val lastName = userFlow.first().lastName
        val gender = userFlow.first().gender
        val email = userFlow.first().email
        val mobileNumber = userFlow.first().mobileNumber
        val imageUrl = userFlow.first().imageUrl
        _settingsUiEvents.emit(SettingsUiEvent.UserData(firstName, lastName, gender, email, mobileNumber, imageUrl))
    }

    fun onLogoutClicked() = viewModelScope.launch{
        firebaseAuthClass.logOutUser()
        _settingsUiEvents.emit(SettingsUiEvent.UserLoggedOut)
    }

    fun onEditClicked() = viewModelScope.launch {
        _settingsUiEvents.emit(SettingsUiEvent.NavigateToProfileFragment)
    }

    sealed class SettingsUiEvent {
        data class UserData(val firstName: String, val lastName: String, val gender: String, val email: String, val mobileNumber: String, val imageUrl: String): SettingsUiEvent()
        object UserLoggedOut: SettingsUiEvent()
        object NavigateToProfileFragment: SettingsUiEvent()
    }
}