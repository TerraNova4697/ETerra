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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepo @Inject constructor() {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(viewModel: RegisterViewModel, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.userRegistrationSuccess()
                } else {
                    viewModel.registrationError()
                }
            }
    }

    fun getUserDetails(viewModel: ViewModel) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(this.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                when (viewModel) {
                    is LoginViewModel -> {
                        viewModel.singInUser(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (viewModel) {
                    is LoginViewModel -> {
                        viewModel.signInFailed(e.message!!)
                    }
                }
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    //TODO: refactor Firebase classes to repository package and make them suspend
    // Make use of this article:
    // https://medium.com/firebase-developers/android-mvvm-firestore-37c3a8d65404
//    suspend fun registerUser(viewModel: RegisterViewModel, userInfo: User) {
//        mFireStore.collection("users")
//            .document(userInfo.id)
//            .set(userInfo, SetOptions.merge())
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    viewModel.userRegistrationSuccess()
//                } else {
//                    viewModel.registrationError()
//                }
//            }
//    }

}