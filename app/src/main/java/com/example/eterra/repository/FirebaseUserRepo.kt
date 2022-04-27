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

    sealed class UserSignInResult() {
        data class Success(val userId: String, val email: String) : UserSignInResult()
        object Failure: UserSignInResult()
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

    fun logInUserWithEmail(email: String, pass: String): UserSignInResult {
        var isSuccessful = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isSuccessful = true
                }
            }
        return if (isSuccessful) {
            UserSignInResult.Success(FirebaseAuth.getInstance().currentUser!!.uid, email)
        } else {
            return UserSignInResult.Failure
        }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

}