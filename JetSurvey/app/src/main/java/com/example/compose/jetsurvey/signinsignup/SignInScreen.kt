/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey.signinsignup

import androidx.compose.Composable
import androidx.compose.launchInComposition
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Snackbar
import androidx.ui.material.TextButton
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.snackbarAction
import kotlinx.coroutines.delay

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object SignUp : SignInEvent()
    object SignInAsGuest : SignInEvent()
}

@Composable
fun SignIn(onEvent: (SignInEvent) -> Unit) {
    val showSnackbar = state { false }
    Stack {
        Column {
            SignInSignUpScreen(
                topAppBarText = stringResource(id = R.string.sign_in),
                onSignedInAsGuest = { onEvent(SignInEvent.SignInAsGuest) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SignInContent(
                        onSignInSubmitted = { email, password ->
                            onEvent(SignInEvent.SignIn(email, password))
                        }
                    )
                    Spacer(modifier = Modifier.preferredHeight(16.dp))
                    TextButton(
                        onClick = { showSnackbar.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.forgot_password),
                            modifier = Modifier.gravity(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
        ErrorSnackbar(
            showError = showSnackbar.value,
            errorText = stringResource(id = R.string.feature_not_available),
            onDismiss = { showSnackbar.value = false },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SignInContent(onSignInSubmitted: (email: String, password: String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val emailState = remember { EmailState() }
        Email(emailState)

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = { onSignInSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = emailState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in),
                modifier = Modifier.gravity(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ErrorSnackbar(
    showError: Boolean,
    errorText: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { }
) {
    if (!showError) {
        return
    }

    // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
    launchInComposition() {
        delay(5000L)
        onDismiss()
    }

    Box(modifier = modifier.fillMaxWidth().wrapContentHeight(Alignment.Bottom)) {
        Crossfade(current = showError) {
            Snackbar(
                modifier = modifier.padding(16.dp),
                text = {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.body2
                    )
                },
                action = {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        contentColor = contentColor()
                    ) {
                        Text(
                            text = stringResource(id = R.string.dismiss),
                            color = MaterialTheme.colors.snackbarAction
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun SignInPreview() {
    JetsurveyTheme {
        SignIn {}
    }
}
