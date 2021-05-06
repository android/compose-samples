package com.example.jetcaster.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow<Screen>(Screen.Home)
    val state: StateFlow<Screen>
        get() = _state

    fun navigateTo(screen: Screen) {
        _state.value = screen
    }

    fun onBack() : Boolean {
        val wasHandled = _state.value != Screen.Home
        _state.value = Screen.Home
        return wasHandled
    }
}