/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey.signinsignup

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.snackbarAction
import kotlinx.coroutines.delay

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object SignUp : SignInEvent()
    object SignInAsGuest : SignInEvent()
    object NavigateBack : SignInEvent()
}

@Composable
fun SignIn(onNavigationEvent: (SignInEvent) -> Unit) {
    val showSnackbar = remember { mutableStateOf(false) }
    Stack(modifier = Modifier.fillMaxSize()) {
        SignInSignUpScreen(
            topAppBarText = stringResource(id = R.string.sign_in),
            onSignedInAsGuest = { onNavigationEvent(SignInEvent.SignInAsGuest) },
            onBackPressed = { onNavigationEvent(SignInEvent.NavigateBack) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SignInContent(
                    onSignInSubmitted = { email, password ->
                        onNavigationEvent(SignInEvent.SignIn(email, password))
                    }
                )
                Spacer(modifier = Modifier.preferredHeight(16.dp))
                TextButton(
                    onClick = { showSnackbar.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.forgot_password))
                }
            }
        }
        ErrorSnackbar(
            showError = showSnackbar.value,
            errorText = stringResource(id = R.string.feature_not_available),
            onDismiss = { showSnackbar.value = false },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun SignInContent(
    onSignInSubmitted: (email: String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester = remember { FocusRequester() }
        val emailState = remember { EmailState() }
        Email(emailState, onImeAction = { focusRequester.requestFocus() })

        Spacer(modifier = Modifier.preferredHeight(16.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { onSignInSubmitted(emailState.text, passwordState.text) }
        )
        Spacer(modifier = Modifier.preferredHeight(16.dp))
        Button(
            onClick = { onSignInSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = emailState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in)
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
                modifier = Modifier.padding(16.dp),
                text = {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.body2
                    )
                },
                action = {
                    TextButton(
                        onClick = onDismiss,
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

@Preview(name = "Sign in light theme")
@Composable
fun SignInPreview() {
    JetsurveyTheme {
        SignIn {}
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    JetsurveyTheme(darkTheme = true) {
        SignIn {}
    }
}
