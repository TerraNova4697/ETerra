package com.example.eterra.ui.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.User
import com.example.eterra.repository.FirebaseAuthClass
import com.example.eterra.repository.FirestoreRepo
import com.example.eterra.repository.PreferencesManager
import com.example.eterra.utils.capitalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuthClass: FirebaseAuthClass,
    private val firestoreRepo: FirestoreRepo,
    private val preferencesManager: PreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val user = preferencesManager.userPreferencesFlow

    private val _loginUiEvents = MutableSharedFlow<LoginUiEvent>()
    val loginUiEvents = _loginUiEvents.asSharedFlow()

    fun onBtnLoginClicked(email: String, password: String) = viewModelScope.launch {
        when {
            TextUtils.isEmpty(email.trim{it <= ' '}) -> {
                _loginUiEvents.emit(LoginUiEvent.EnterEmailError)
                return@launch
            }
            TextUtils.isEmpty(password.trim{it <= ' '}) -> {
                _loginUiEvents.emit(LoginUiEvent.EnterPassword)
                return@launch
            }
            else -> {
                _loginUiEvents.emit(LoginUiEvent.SigningInInProgress)
                val loginResult = firebaseAuthClass.loginUser(email.trim{it <= ' '}, password.trim{it <= ' '})
                when (loginResult) {
                    is FirebaseAuthClass.LoginResult.Success -> {
                        val getUserResult = firestoreRepo.getUserDetails()
                        when (getUserResult) {
                            is FirestoreRepo.GetUserResult.Success -> {
                                val gender = getUserResult.user.gender.capitalized()
                                val mobileNumber = getUserResult.user.mobile.toString()
                                preferencesManager.saveUser(
                                    firstName = getUserResult.user.firstName,
                                    lastName = getUserResult.user.lastName,
                                    gender = gender,
                                    email = getUserResult.user.email,
                                    mobileNumber = mobileNumber,
                                    imageUrl = getUserResult.user.image
                                )
                                singInUser(getUserResult.user)
                            }
                            is FirestoreRepo.GetUserResult.Failure -> {
                                signInFailed(getUserResult.errorMessage)
                            }
                        }
                    }
                    is FirebaseAuthClass.LoginResult.Failure -> {
                        signInFailed(loginResult.errorMessage)
                    }
                }
            }
        }
    }

    fun singInUser(user: User) = viewModelScope.launch{
        _loginUiEvents.emit(LoginUiEvent.SignInSuccess(user))
    }

    fun signInFailed(exception: String) = viewModelScope.launch {
        _loginUiEvents.emit(LoginUiEvent.SignInFailed(exception))
    }

    sealed class LoginUiEvent() {
        object EnterEmailError: LoginUiEvent()
        object EnterPassword: LoginUiEvent()
//        data class SignInSuccess(val userId: String, val email: String): LoginUiEvent()
        data class SignInSuccess(val user: User): LoginUiEvent()
        data class SignInFailed(val exception: String): LoginUiEvent()
        object SigningInInProgress: LoginUiEvent()
    }

}