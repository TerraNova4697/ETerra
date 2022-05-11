package com.example.eterra.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _mainViewModelEvents: MutableSharedFlow<MainUiEvent> = MutableSharedFlow()
    val mainViewModelEvents = _mainViewModelEvents.asSharedFlow()

    sealed class MainUiEvent {

    }

}