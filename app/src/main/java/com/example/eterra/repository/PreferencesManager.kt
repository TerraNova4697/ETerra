package com.example.eterra.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.eterra.utils.Constants
import com.example.eterra.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class UserPreferences(
    val firstName: String,
    val lastName: String,
    val gender: String,
    val email: String,
    val mobileNumber: String,
    val imageUrl: String
)

val LOGGED_IN_FIRST_NAME = stringPreferencesKey("logged_in_first_name")
val LOGGED_IN_LAST_NAME = stringPreferencesKey("logged_in_last_name")
val LOGGED_IN_GENDER = stringPreferencesKey("logged_in_gender")
val LOGGED_IN_EMAIL = stringPreferencesKey("logged_in_email")
val LOGGED_IN_MOBILE_NUMBER = stringPreferencesKey("logged_in_mobile_number")
val IMAGE_URL = stringPreferencesKey("image_url")

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(Constants.PREFERENCES_TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val firstName: String = preferences[LOGGED_IN_FIRST_NAME] ?: ""
            val lastName: String = preferences[LOGGED_IN_LAST_NAME] ?: ""
            val gender: String = preferences[LOGGED_IN_GENDER] ?: "Male"
            val email: String = preferences[LOGGED_IN_EMAIL] ?: ""
            val mobileNumber: String = preferences[LOGGED_IN_MOBILE_NUMBER] ?: "0"
            val imageUrl: String = preferences[IMAGE_URL] ?: ""
            UserPreferences(firstName, lastName, gender, email, mobileNumber, imageUrl)
        }

    suspend fun saveUser(firstName: String, lastName: String, gender: String, email: String, mobileNumber: String, imageUrl: String) {
        context.dataStore.edit { settings ->
            settings[LOGGED_IN_FIRST_NAME] = firstName
            settings[LOGGED_IN_LAST_NAME] = lastName
            settings[LOGGED_IN_GENDER] = gender
            settings[LOGGED_IN_EMAIL] = email
            settings[LOGGED_IN_MOBILE_NUMBER] = mobileNumber
            settings[IMAGE_URL] = imageUrl
        }
    }

}