package com.example.eterra.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthClass @Inject constructor() {

    suspend fun loginUser(email: String, password: String): LoginResult {
        var isSuccessful = false
        var errorMessage = "Oops... Something went wrong. Please try again."
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccessful = true
                    } else {
                        errorMessage = task.exception?.message.toString()
                    }
                }
                .await()
            return if (isSuccessful) {
                LoginResult.Success
            } else {
                LoginResult.Failure(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, e.message.toString())
        }
        return LoginResult.Failure(errorMessage)
    }

    suspend fun authorizeUser(email: String, password: String): AuthorizationResult {
        return try {
            val firebaseUser =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .await().user
            if (firebaseUser != null) {
                AuthorizationResult.Success(firebaseUser.uid)
            } else {
                AuthorizationResult.Failure("Oops... Something went wrong. Please try again")
            }
        } catch (e: Exception) {
            AuthorizationResult.Failure(e.message ?: "Oops... Something went wrong. Please try again")
        }
    }

    sealed class LoginResult {
        object Success: LoginResult()
        data class Failure(val errorMessage: String): LoginResult()
    }

    sealed class AuthorizationResult {
        data class Success(val uid: String): AuthorizationResult()
        data class Failure(val message: String): AuthorizationResult()
    }

    suspend fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
    }

}
