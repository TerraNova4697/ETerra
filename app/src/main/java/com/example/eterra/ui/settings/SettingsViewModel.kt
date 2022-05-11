package com.example.eterra.ui.settings

import androidx.lifecycle.ViewModel
import com.example.eterra.repository.FirebaseStorageClass
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseStorageClass: FirebaseStorageClass
): ViewModel() {
}