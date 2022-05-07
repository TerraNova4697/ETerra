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
    val name: String
)

val LOGGED_IN_USERNAME = stringPreferencesKey("logged_in_username")

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
            val userName: String = preferences[LOGGED_IN_USERNAME] ?: ""
            UserPreferences(userName)
        }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { settings ->
            settings[LOGGED_IN_USERNAME] = name
        }
    }

}