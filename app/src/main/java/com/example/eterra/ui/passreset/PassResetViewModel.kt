package com.example.eterra.ui.passreset

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
class PassResetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _passResetUiEvents: MutableSharedFlow<PassResetUiEvents> = MutableSharedFlow()
    val passResetUiEvents = _passResetUiEvents.asSharedFlow()

    fun onSubmitClicked(email: String) = viewModelScope.launch {
        val resetEmail = email.trim{it <= ' '}
        if (resetEmail.isEmpty()) {
            _passResetUiEvents.emit(PassResetUiEvents.EnterEmailError)
        } else {
            _passResetUiEvents.emit(PassResetUiEvents.PassResetInProgress)
            FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        notifyEmailSent()
                    } else {
                        notifyExceptionWhilePassReset(task.exception?.message?.toString())
                    }
                }
        }
    }

    private fun notifyEmailSent() = viewModelScope.launch {
        _passResetUiEvents.emit(PassResetUiEvents.EmailSent)
    }

    private fun notifyExceptionWhilePassReset(msg: String?) = viewModelScope.launch {
        _passResetUiEvents.emit(PassResetUiEvents.ExceptionWhilePassReset(msg ?: "Oops, something went wrong"))
    }

    sealed class PassResetUiEvents() {
        object EnterEmailError: PassResetUiEvents()
        object EmailSent: PassResetUiEvents()
        data class ExceptionWhilePassReset(val msg: String): PassResetUiEvents()
        object PassResetInProgress: PassResetUiEvents()
    }

}