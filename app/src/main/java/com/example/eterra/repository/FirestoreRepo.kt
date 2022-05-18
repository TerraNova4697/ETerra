package com.example.eterra.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eterra.models.CartItem
import com.example.eterra.models.Product
import com.example.eterra.models.User
import com.example.eterra.ui.login.LoginViewModel
import com.example.eterra.ui.register.RegisterViewModel
import com.example.eterra.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.callbackFlow
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

    suspend fun updateUserDetails(userHashMap: HashMap<String, Any>): UpdateUserDetails {
        return try {
            var isSuccessful = false
            mFireStore
                .collection(Constants.USERS)
                .document(getCurrentUserId())
                .update(userHashMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccessful = true
                    } else {
                        Log.e(this.javaClass.simpleName, task.exception?.message ?: "Error while updating")
                    }
                }
                .await()
            if (isSuccessful) {
                return UpdateUserDetails.Success
            } else {
                return UpdateUserDetails.Failure("Error while updating")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return UpdateUserDetails.Failure(e.message ?: "Error while updating")
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

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

    suspend fun uploadProductDetails(productInfo: Product): UploadProduct {
        try {
            var isSuccessful = false
            mFireStore
                .collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isSuccessful = true
                    }
                }
                .await()
            if (isSuccessful) {
                return UploadProduct.Success
            } else {
                return UploadProduct.Failure("Oops... something went wrong. Please try again.")
            }
        } catch (e: Exception) {
            return UploadProduct.Failure(e.message ?: "Oops... something went wrong. Please try again.")
        }
    }

    suspend fun getUsersProductsList(): LoadUsersProductsList {

        try {
            val snapshot = mFireStore
                .collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserId())
                .get()
                .await()
            val listOfProducts: ArrayList<Product> = ArrayList()
            for (i in snapshot.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id
                listOfProducts.add(product)
            }
            return LoadUsersProductsList.Success(listOfProducts)
        } catch (e: Exception) {
            Log.e(this@FirestoreRepo.javaClass.simpleName, e.message.toString())
            return LoadUsersProductsList.Failure("Oops, something went wrong")
        }
    }

    suspend fun getProductsList(): LoadUsersProductsList {
        try {
            val snapshot = mFireStore
                .collection(Constants.PRODUCTS)
                .get()
                .await()
            val listOfProducts: ArrayList<Product> = ArrayList()
            for (i in snapshot.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id
                listOfProducts.add(product)
            }
            return LoadUsersProductsList.Success(listOfProducts)
        } catch (e: Exception) {
            Log.e(this@FirestoreRepo.javaClass.simpleName, e.message.toString())
            return LoadUsersProductsList.Failure("Oops, something went wrong")
        }
    }

    suspend fun deleteProduct(productId: String): DeleteResult {
        return try {
            mFireStore
                .collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .await()
            DeleteResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            DeleteResult.Failure
        }
    }

    suspend fun getProductDetails(productId: String): ProductDetails {
        return try {
            val document = mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .await()
            val product = document.toObject(Product::class.java)
            ProductDetails.Success(product!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ProductDetails.Failure(e.message ?: "Something went wrong")
        }
    }

    suspend fun addCartItems(addToCart: CartItem): AddToCart {
        return try {
            mFireStore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .await()
            AddToCart.Success
        } catch (e: Exception) {
            e.printStackTrace()
            AddToCart.Failure(e.message ?: "Something went wrong")
        }
    }

    sealed class AddToCart {
        object Success: AddToCart()
        data class Failure(val errorMessage: String): AddToCart()
    }

    sealed class ProductDetails {
        data class Success(val product: Product): ProductDetails()
        data class Failure(val errorMessage: String): ProductDetails()
    }

    sealed class DeleteResult {
        object Success: DeleteResult()
        object Failure: DeleteResult()
    }

    sealed class LoadUsersProductsList {
        data class Success(val products: ArrayList<Product>): LoadUsersProductsList()
        data class Failure(val errorMessage: String): LoadUsersProductsList()
    }

    sealed class UploadProduct {
        object Success: UploadProduct()
        data class Failure(val message: String): UploadProduct()
    }

    sealed class UpdateUserDetails {
        object Success: UpdateUserDetails()
        data class Failure(val message: String): UpdateUserDetails()
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