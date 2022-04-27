package com.example.eterra.ui.login

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.repository.FirebaseUserRepo
import com.example.eterra.ui.register.RegisterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseUserRepo: FirebaseUserRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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
        }
        when (val signInResult = firebaseUserRepo.logInUserWithEmail(email, password)) {
            is FirebaseUserRepo.UserSignInResult.Success -> {
                _loginUiEvents.emit(LoginUiEvent.SignInSuccess(signInResult.userId, signInResult.email))
            }
            is FirebaseUserRepo.UserSignInResult.Failure -> {
                _loginUiEvents.emit(LoginUiEvent.SignInFailed)
            }
        }

    }

    sealed class LoginUiEvent() {
        object EnterEmailError: LoginUiEvent()
        object EnterPassword: LoginUiEvent()
        data class SignInSuccess(val userId: String, val email: String): LoginUiEvent()
        object SignInFailed: LoginUiEvent()
    }

}