package com.example.eterra.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepo @Inject constructor() {

    sealed class UserRegistrationResult() {
        data class Success(var user: FirebaseUser?): UserRegistrationResult()
        object Failure: UserRegistrationResult()
    }

    fun createUserWithEmail(email: String, pass: String): UserRegistrationResult {
        var isSuccessful = false
        var user: FirebaseUser? = null
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isSuccessful = true
                    user = task.result!!.user!!
                }
            }
            .addOnFailureListener {
                Log.d("Registration", "Oops, smth wrong while registration ${it.message}")
            }
        return if (isSuccessful) {
            UserRegistrationResult.Success(user)
        } else {
            UserRegistrationResult.Failure
        }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

}