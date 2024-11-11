package com.example.jetnews.ui.signup

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.authentication.AuthRepository
import com.example.jetnews.model.authentication.AuthResult
import com.example.jetnews.model.authentication.AuthUiState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignUpScreenViewModel(
    private val context: Context,
) : ViewModel() {
    private val repository = AuthRepository()

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _uiState = mutableStateOf<AuthUiState>(AuthUiState.Initial)
    val uiState: State<AuthUiState> = _uiState

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onConfirmPasswordChange(newValue: String) {
        _confirmPassword.value = newValue
    }

    fun onSignUpClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val result =
                    repository.createUserWithEmailAndPassword(
                        name = _name.value.trim(),
                        email = _email.value.trim(),
                        password = _password.value,
                    )
                handleAuthResult(result)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun handleError(message: String) {
        Log.e("SignUpError", message)
        _uiState.value = AuthUiState.Error(message)
    }

    private fun handleAuthResult(result: AuthResult<FirebaseUser>) {
        _uiState.value =
            when (result) {
                is AuthResult.Success -> AuthUiState.Success
                is AuthResult.Error -> AuthUiState.Error(result.message)
            }
    }

    private fun validateInputs(): Boolean {
        if (_name.value.isEmpty() ||
            _email.value.isEmpty() ||
            _password.value.isEmpty() ||
            _confirmPassword.value.isEmpty()
        ) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(_email.value)
                .matches()
        ) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address")
            return false
        }
        if (_password.value.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return false
        }
        if (_password.value != _confirmPassword.value) {
            _uiState.value = AuthUiState.Error("Passwords do not match")
            return false
        }
        return true
    }
}
