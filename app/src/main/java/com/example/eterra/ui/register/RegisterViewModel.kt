package com.example.eterra.ui.register

import android.text.TextUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.User
import com.example.eterra.repository.FirebaseAuthClass
import com.example.eterra.repository.FirestoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuthClass,
    private val savedStateHandle: SavedStateHandle,
    private val firestoreRepo: FirestoreRepo
): ViewModel() {

    // TODO: Rename variables more comprehensive

    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class RegisterUiEvent() {
        // Registration events
        object EnterFirstNameError: RegisterUiEvent()
        object EnterLastNameError: RegisterUiEvent()
        object EnterEmailError: RegisterUiEvent()
        object EnterPassword: RegisterUiEvent()
        object PasswordNotMatch: RegisterUiEvent()
        object ValidData: RegisterUiEvent()
        object SignInUser: RegisterUiEvent()
        data class ErrorWhileRegistering(val message: String): RegisterUiEvent()
        object RegisteringInProgress: RegisterUiEvent()
    }

    fun onBtnRegisterClicked(firstName: String, lastName: String, email: String, password: String, passwordConfirm: String) = viewModelScope.launch {
        when {
                TextUtils.isEmpty(firstName.trim {it <= ' '}) -> {
                _uiEvent.emit(RegisterUiEvent.EnterFirstNameError)
                return@launch
            }
            TextUtils.isEmpty(lastName.trim {it <= ' '}) -> {
                _uiEvent.emit(RegisterUiEvent.EnterLastNameError)
                return@launch
            }
            TextUtils.isEmpty(email.trim {it <= ' '}) -> {
                _uiEvent.emit(RegisterUiEvent.EnterEmailError)
                return@launch
            }
            TextUtils.isEmpty(password.trim {it <= ' '}) -> {
                _uiEvent.emit(RegisterUiEvent.EnterPassword)
                return@launch
            }
            (password.trim {it <= ' '} != passwordConfirm.trim {it <= ' '}) -> {
                _uiEvent.emit(RegisterUiEvent.PasswordNotMatch)
                return@launch
            }
            else -> {
                _uiEvent.emit(RegisterUiEvent.RegisteringInProgress)
                val authorizationResult = firebaseAuth.authorizeUser(email, password)
                when (authorizationResult) {
                    is FirebaseAuthClass.AuthorizationResult.Success -> {
                        val user = User(
                            authorizationResult.uid,
                            firstName.trim {it <= ' '},
                            lastName.trim {it <= ' '},
                            email.trim {it <= ' '}
                        )
                        val registrationResult = firestoreRepo.registerUser(user)
                        when (registrationResult) {
                            is FirestoreRepo.RegistrationResult.Success -> {
                                userRegistrationSuccess()
                            }
                            is FirestoreRepo.RegistrationResult.Failure -> {
                                registrationError(registrationResult.message)
                            }
                        }
                    }
                    is FirebaseAuthClass.AuthorizationResult.Failure -> {
                        registrationError(authorizationResult.message)
                    }
                }
            }
        }
    }

    fun userRegistrationSuccess() = viewModelScope.launch{
        _uiEvent.emit(RegisterUiEvent.SignInUser)
    }

    fun registrationError(message: String) = viewModelScope.launch {
        _uiEvent.emit(RegisterUiEvent.ErrorWhileRegistering(message))
    }
}