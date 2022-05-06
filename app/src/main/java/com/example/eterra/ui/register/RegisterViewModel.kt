package com.example.eterra.ui.register

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eterra.models.User
import com.example.eterra.repository.FirestoreRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
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
        object ErrorWhileRegistering: RegisterUiEvent()
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
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLog", "task is: ${task.isSuccessful}")
                            val user = User(
                                task.result.user?.uid ?: "",
                                firstName.trim {it <= ' '},
                                lastName.trim {it <= ' '},
                                email.trim {it <= ' '}
                            )
                            firestoreRepo.registerUser(this@RegisterViewModel, user)
                        }
                    }
                    .addOnFailureListener {
                        registrationError()
                        Log.d("MyLog", "Oops, smth wrong while registration ${it.message}")
                    }
            }
        }
    }

    fun userRegistrationSuccess() = viewModelScope.launch{
        _uiEvent.emit(RegisterUiEvent.SignInUser)
    }

    fun registrationError() = viewModelScope.launch {
        _uiEvent.emit(RegisterUiEvent.ErrorWhileRegistering)
    }
}