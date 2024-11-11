package com.example.jetnews.ui.signin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.authentication.AuthRepository
import com.example.jetnews.model.authentication.AuthResult
import com.example.jetnews.model.authentication.AuthUiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignInScreenViewModel(
    private val context: Context,
) : ViewModel() {
    private val repository = AuthRepository()

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _uiState = mutableStateOf<AuthUiState>(AuthUiState.Initial)
    val uiState: State<AuthUiState> = _uiState

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthResult<GoogleSignInAccount> =
        try {
            val account = task.getResult(ApiException::class.java)
            AuthResult.Success(account)
        } catch (e: ApiException) {
            AuthResult.Error(e.message ?: "Google sign-in failed")
        }

    fun handleGoogleCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.signInWithGoogle(credential)
            handleAuthResult(result)
        }
    }

    fun handleError(message: String) {
        Log.e("SignInError", message)
        _uiState.value = AuthUiState.Error(message)
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onSignInClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val result =
                    repository.signInWithEmailAndPassword(
                        email = _email.value.trim(),
                        password = _password.value,
                    )
                handleAuthResult(result)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    private fun handleAuthResult(result: AuthResult<FirebaseUser>) {
        _uiState.value =
            when (result) {
                is AuthResult.Success -> AuthUiState.Success
                is AuthResult.Error -> AuthUiState.Error(result.message)
            }
    }

    private fun validateInputs(): Boolean {
        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(_email.value)
                .matches()
        ) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address")
            return false
        }
        return true
    }
}
