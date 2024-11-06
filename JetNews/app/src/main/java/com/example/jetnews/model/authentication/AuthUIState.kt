package com.example.jetnews.model.authentication

sealed class AuthUiState {
    object Initial : AuthUiState()

    object Loading : AuthUiState()

    object Success : AuthUiState()

    data class Error(
        val message: String,
    ) : AuthUiState()
}
