package com.example.eterra.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eterra.models.User
import com.example.eterra.ui.login.LoginViewModel
import com.example.eterra.ui.register.RegisterViewModel
import com.example.eterra.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepo @Inject constructor() {

    private val mFireStore = FirebaseFirestore.getInstance()

    suspend fun getUserDetails(): GetUserResult {
        return try {
            val document = mFireStore
                .collection(Constants.USERS)
                .document(getCurrentUserId())
                .get()
                .await()
            val user = document.toObject(User::class.java)!!
            GetUserResult.Success(user)
        } catch (e: java.lang.Exception) {
            Log.e(this.javaClass.simpleName, e.message.toString())
            GetUserResult.Failure(e.message.toString())
        }
    }

    private fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    //TODO: refactor Firebase classes to repository package and make them suspend
    // Make use of this article:
    // https://medium.com/firebase-developers/android-mvvm-firestore-37c3a8d65404
    suspend fun registerUser(userInfo: User): RegistrationResult {
        try {
            var isSuccessful = false
            mFireStore
                .collection(Constants.USERS)
                .document(userInfo.id)
                .set(userInfo, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccessful = true
                    }
                }
                .await()
            if (isSuccessful) {
                return RegistrationResult.Success
            } else {
                return RegistrationResult.Failure("Oops... something went wrong. Please try again.")
            }
        } catch (e: Exception) {
            return RegistrationResult.Failure(e.message ?: "Oops... something went wrong. Please try again.")
        }
    }

    sealed class GetUserResult {
        data class Success(val user: User): GetUserResult()
        data class Failure(val errorMessage: String): GetUserResult()
    }

    sealed class RegistrationResult {
        object Success: RegistrationResult()
        data class Failure(val message: String): RegistrationResult()
    }

}