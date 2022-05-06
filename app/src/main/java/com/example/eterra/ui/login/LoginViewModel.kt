package com.example.eterra.ui.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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
            else -> {
                _loginUiEvents.emit(LoginUiEvent.SigningInInProgress)
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            singInUser(FirebaseAuth.getInstance().currentUser!!.uid, email)
                        } else {
                            Log.e(this.javaClass.name, task.exception?.message ?: "Untracked exception")
                            signInFailed(task.exception?.message ?: "Oops, something went wrong")
                        }
                    }
//                    .addOnFailureListener {
//                        signInFailed()
//                    }
            }
        }
    }

    private fun singInUser(uid: String, email: String) = viewModelScope.launch{
        _loginUiEvents.emit(LoginUiEvent.SignInSuccess(uid, email))
    }

    private fun signInFailed(exception: String) = viewModelScope.launch {
        _loginUiEvents.emit(LoginUiEvent.SignInFailed(exception))
    }

    sealed class LoginUiEvent() {
        object EnterEmailError: LoginUiEvent()
        object EnterPassword: LoginUiEvent()
        data class SignInSuccess(val userId: String, val email: String): LoginUiEvent()
        data class SignInFailed(val exception: String): LoginUiEvent()
        object SigningInInProgress: LoginUiEvent()
    }

}