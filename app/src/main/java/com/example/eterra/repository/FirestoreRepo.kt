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

}