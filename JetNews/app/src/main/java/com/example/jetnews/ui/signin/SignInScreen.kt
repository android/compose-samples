package com.example.jetnews.ui.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.R
import com.example.jetnews.model.authentication.AuthResult
import com.example.jetnews.model.authentication.AuthUiState
import com.example.jetnews.ui.JetnewsDestinations
import com.example.jetnews.ui.ViewModelFactory
import com.example.jetnews.ui.components.buttons.AuthButton
import com.example.jetnews.ui.components.buttons.SocialAuthButton
import com.example.jetnews.ui.components.textfields.AuthTextField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignInScreen(
    navHostController: NavHostController,
    viewModel: SignInScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val analytics = Firebase.analytics // Inicialización de Firebase Analytics
    // State
    val email by viewModel.email
    val password by viewModel.password
    val uiState by viewModel.uiState

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            // Registrar evento de inicio de sesión en Analytics cuando el usuario inicia sesión con éxito
            analytics.logEvent("user_sign_in") {
                param("sign_in_method", "email") // Aquí podrías especificar "email" u otro método
            }
            navHostController.navigate(JetnewsDestinations.HOME_ROUTE) {
                popUpTo(JetnewsDestinations.SIGNIN_ROUTE) { inclusive = true }
            }
        }
    }

    // ActivityResultLauncher for Google Sign-In
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            when (val accountResult = viewModel.handleSignInResult(task)) {
                is AuthResult.Success -> {
                    val account = accountResult.data
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    scope.launch {
                        viewModel.handleGoogleCredential(credential)
                        analytics.logEvent("user_sign_in") {
                            param("sign_in_method", "google")
                        }
                    }
                }
                is AuthResult.Error -> {
                    viewModel.handleError(accountResult.message)
                }
                else -> {
                    viewModel.handleError("Unknown error during Google Sign-In")
                }
            }
        }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Form
            AuthTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
            )

            AuthTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                visualTransformation = PasswordVisualTransformation(),
            )

            // Forgot Password
            TextButton(
                onClick = { /* TODO: Navigate to forgot password */ },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Forgot Password?")
            }

            // Error message
            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Sign In Button
            AuthButton(
                text = "Sign In",
                onClick = {
                    viewModel.onSignInClick()
                    // Registrar evento de intento de inicio de sesión
                    analytics.logEvent("attempt_user_sign_in") {
                        param("sign_in_method", "email")
                    }
                },
                isLoading = uiState is AuthUiState.Loading,
            )

            // OR Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Divider(modifier = Modifier.weight(1f))
            }

            SocialAuthButton(
                text = "Sign in with Google",
                onClick = {
                    viewModel.signInWithGoogle(googleSignInLauncher)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified,
                    )
                },
            )
            // Sign Up Link
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = {
                    navHostController.navigate(JetnewsDestinations.SIGNUP_ROUTE)
                }) {
                    Text("Sign Up")
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(
    name = "Sign In Light Theme",
    showBackground = true,
    heightDp = 800,
)
@Composable
fun SignInScreenPreview() {
    val previewNavController = rememberNavController()
    val context = LocalContext.current
    val viewModel: SignInScreenViewModel =
        viewModel(
            factory = ViewModelFactory(context),
        )

    MaterialTheme(
        colorScheme = lightColorScheme(),
    ) {
        SignInScreen(
            navHostController = previewNavController,
            viewModel = viewModel,
        )
    }
}
