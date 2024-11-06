package com.example.jetnews.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.authentication.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<Boolean?>(null)
    val authState: StateFlow<Boolean?> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _authState.value = repository.getCurrentUser() != null
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = false
        }
    }
}
