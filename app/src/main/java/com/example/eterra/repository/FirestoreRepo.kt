package com.example.eterra.repository

import com.example.eterra.models.User
import com.example.eterra.ui.register.RegisterViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepo @Inject constructor() {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(viewModel: RegisterViewModel, userInfo: User) {
        mFireStore.collection("users")
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