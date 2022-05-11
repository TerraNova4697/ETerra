package com.example.eterra.repository

import android.net.Uri
import android.util.Log
import com.example.eterra.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageClass @Inject constructor() {

    suspend fun uploadProfileImage(
        selectedImageUri: Uri,
        extensionType: String
    ): UploadProfileImage {


        var errorMessage = "Error while image upload"

        val profileImgReference: StorageReference = FirebaseStorage
            .getInstance()
            .reference
            .child(Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + extensionType)

        try {

            var uploadedImageUri: Uri? = null
//            var isSuccessful = false

            val taskSnapshot = profileImgReference.putFile(selectedImageUri).await()

            uploadedImageUri = taskSnapshot.metadata!!.reference!!.downloadUrl.await()

            Log.e(
                this@FirebaseStorageClass.javaClass.simpleName,
                "Image URI: $uploadedImageUri"
            )
            return if (uploadedImageUri != null) {
                UploadProfileImage.Success(uploadedImageUri)
            } else {
                UploadProfileImage.Failure(errorMessage)
            }

        } catch (e: Exception) {
            Log.e(
                this@FirebaseStorageClass.javaClass.simpleName,
                e.message ?: "Error while image upload"
            )
            e.message.let { errorMessage = it!! }
            return UploadProfileImage.Failure(errorMessage)
        }

    }

    sealed class UploadProfileImage {
        data class Success(val imageUri: Uri) : UploadProfileImage()
        data class Failure(val message: String) : UploadProfileImage()
    }

}