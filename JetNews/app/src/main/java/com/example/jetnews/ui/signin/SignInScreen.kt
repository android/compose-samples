package com.example.jetnews.ui.signin

import android.content.res.Configuration
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.ui.JetnewsDestinations
import com.example.jetnews.ui.components.buttons.AuthButton
import com.example.jetnews.ui.components.buttons.SocialAuthButton
import com.example.jetnews.ui.components.textfields.AuthTextField

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignInScreen(
    navHostController: NavHostController,
    signInViewModel: SignInScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val email by signInViewModel.email
    val password by signInViewModel.password
    val isLoading by signInViewModel.isLoading
    val error by signInViewModel.error

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
                onValueChange = signInViewModel::onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
            )

            AuthTextField(
                value = password,
                onValueChange = signInViewModel::onPasswordChange,
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

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Sign In Button
            AuthButton(
                text = "Sign In",
                onClick = signInViewModel::onSignInClick,
                isLoading = isLoading,
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

            // Social Sign In
            SocialAuthButton(
                text = "Continue with Google",
                onClick = { /* TODO */ },
                icon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info), // Replace with Google icon
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp),
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignInScreenPreview() {
    val previewNavController = rememberNavController()
    val previewViewModel = SignInScreenViewModel()

    MaterialTheme {
        SignInScreen(
            navHostController = previewNavController,
            signInViewModel = previewViewModel,
        )
    }
}
