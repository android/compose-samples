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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
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
import com.example.jetnews.ui.components.buttons.AuthButton
import com.example.jetnews.ui.components.buttons.SocialAuthButton
import com.example.jetnews.ui.components.textfields.AuthTextField

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpScreen(
    navHostController: NavHostController,
    signUpViewModel: SignUpScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val name by signUpViewModel.name
    val email by signUpViewModel.email
    val password by signUpViewModel.password
    val confirmPassword by signUpViewModel.confirmPassword
    val isLoading by signUpViewModel.isLoading
    val error by signUpViewModel.error

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
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

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

            Spacer(modifier = Modifier.height(8.dp))

            // Form
            AuthTextField(
                value = name,
                onValueChange = signUpViewModel::onNameChange,
                label = "Full Name",
            )

            AuthTextField(
                value = email,
                onValueChange = signUpViewModel::onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
            )

            AuthTextField(
                value = password,
                onValueChange = signUpViewModel::onPasswordChange,
                label = "Password",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthTextField(
                value = confirmPassword,
                onValueChange = signUpViewModel::onConfirmPasswordChange,
                label = "Confirm Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                visualTransformation = PasswordVisualTransformation(),
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Sign Up Button
            AuthButton(
                text = "Create Account",
                onClick = signUpViewModel::onSignUpClick,
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

            // Social Sign Up
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

            // Sign In Link
            Row(
                modifier = Modifier.padding(top = 8.dp),
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
    val previewViewModel = SignUpScreenViewModel()

    MaterialTheme(
        colorScheme = lightColorScheme(),
    ) {
        Surface {
            SignUpScreen(
                navHostController = previewNavController,
                signUpViewModel = previewViewModel,
            )
        }
    }
}
