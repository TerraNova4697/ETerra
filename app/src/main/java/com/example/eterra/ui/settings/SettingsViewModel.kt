package com.example.eterra.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.User
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
    private val preferencesManager: PreferencesManager
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
        val name = userFlow.first().name
        val gender = userFlow.first().gender
        val email = userFlow.first().email
        val mobileNumber = userFlow.first().mobileNumber
        val imageUrl = userFlow.first().imageUrl
        Log.d(this@SettingsViewModel.javaClass.simpleName, "$name, $gender, $email, $mobileNumber")
        _settingsUiEvents.emit(SettingsUiEvent.UserData(name, gender, email, mobileNumber, imageUrl))
    }

    sealed class SettingsUiEvent {
        data class UserData(val name: String, val gender: String, val email: String, val mobileNumber: String, val imageUrl: String): SettingsUiEvent()
    }
}