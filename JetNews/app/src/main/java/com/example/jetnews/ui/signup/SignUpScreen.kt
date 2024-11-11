package com.example.jetnews.ui.signup

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
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.model.authentication.AuthUiState
import com.example.jetnews.ui.JetnewsDestinations
import com.example.jetnews.ui.ViewModelFactory
import com.example.jetnews.ui.components.buttons.AuthButton
import com.example.jetnews.ui.components.textfields.AuthTextField

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(
    navHostController: NavHostController,
    viewModel: SignUpScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // State
    val name by viewModel.name
    val email by viewModel.email
    val password by viewModel.password
    val confirmPassword by viewModel.confirmPassword
    val uiState by viewModel.uiState

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            navHostController.navigate(JetnewsDestinations.HOME_ROUTE) {
                popUpTo(JetnewsDestinations.SIGNUP_ROUTE) { inclusive = true }
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
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Header
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form
            AuthTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = "Full Name",
            )

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
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthTextField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirm Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                visualTransformation = PasswordVisualTransformation(),
            )

            // Error message
            if (uiState is AuthUiState.Error) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            AuthButton(
                text = "Create Account",
                onClick = viewModel::onSignUpClick,
                isLoading = uiState is AuthUiState.Loading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign In Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = {
                    navHostController.navigateUp()
                }) {
                    Text("Sign In")
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(
    name = "Sign Up Light Theme",
    showBackground = true,
    heightDp = 800,
)
@Composable
fun SignUpScreenLightPreview() {
    val previewNavController = rememberNavController()
    val context = LocalContext.current
    val viewModel: SignUpScreenViewModel =
        viewModel(
            factory = ViewModelFactory(context),
        )

    MaterialTheme(
        colorScheme = lightColorScheme(),
    ) {
        Surface {
            SignUpScreen(
                navHostController = previewNavController,
                viewModel = viewModel,
            )
        }
    }
}
