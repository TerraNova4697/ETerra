package com.example.eterra.ui.register

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.repository.FirebaseUserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseUserRepo: FirebaseUserRepo
): ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent() {
        // Registration events
        object EnterEmailError: UiEvent()
        object EnterPassword: UiEvent()
        object PasswordNotMatch: UiEvent()
        object ValidData: UiEvent()
        data class SignInUser(val userId: String, val email: String): UiEvent()
        object ErrorWhileRegistering: UiEvent()
    }

    fun onBtnRegisterClicked(email: String, password: String, passwordConfirm: String) = viewModelScope.launch {
        when {
            TextUtils.isEmpty(email.trim {it <= ' '}) -> {
                _uiEvent.emit(UiEvent.EnterEmailError)
                return@launch
            }
            TextUtils.isEmpty(password.trim {it <= ' '}) -> {
                _uiEvent.emit(UiEvent.EnterPassword)
                return@launch
            }
            (password.trim {it <= ' '} != passwordConfirm.trim {it <= ' '}) -> {
                _uiEvent.emit(UiEvent.PasswordNotMatch)
                return@launch
            }
            // TODO: Add progressbar
        }
        val result = firebaseUserRepo.createUserWithEmail(email, password)
        when (result) {
            is FirebaseUserRepo.UserRegistrationResult.Success -> {
                _uiEvent.emit(UiEvent.SignInUser(result.user!!.uid, result.user!!.email!!))
            }
            is FirebaseUserRepo.UserRegistrationResult.Failure -> {
                _uiEvent.emit(UiEvent.ErrorWhileRegistering)
            }
        }
    }

}