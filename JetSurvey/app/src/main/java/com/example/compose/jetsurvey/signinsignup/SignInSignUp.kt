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
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.currentTextStyle
import androidx.ui.input.PasswordVisualTransformation
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.OutlinedButton
import androidx.ui.material.OutlinedTextField
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Surface
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ChevronLeft
import androidx.ui.material.icons.filled.Visibility
import androidx.ui.material.icons.filled.VisibilityOff
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R

@Composable
fun SignInSignUpScreen(
    topAppBarText: String,
    onSignedInAsGuest: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable() () -> Unit
) {
    Column(modifier = modifier) {
        SignInSignUpTopAppBar(topAppBarText, onBackPressed)
        VerticalScroller {
            Spacer(modifier = Modifier.preferredHeight(44.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                content()
            }
            Spacer(modifier = Modifier.preferredHeight(16.dp))
            OrSignInAsGuest(
                onSignedInAsGuest = onSignedInAsGuest,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun SignInSignUpTopAppBar(topAppBarText: String, onBackPressed: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = topAppBarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ChevronLeft)
            }
        },
        // We need to balance the navigation icon, so we add a spacer.
        actions = {
            Spacer(modifier = Modifier.preferredWidth(68.dp))
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}

@Composable
fun Email(emailState: FilledTextFieldState = remember { EmailState() }) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = { emailState.text = it },
        onFocusChange = { focused ->
            emailState.onFocusChange(focused)
            if (!focused) {
                emailState.enableShowErrors()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        label = {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(
                    text = stringResource(id = R.string.email),
                    style = MaterialTheme.typography.body2
                )
            }
        },
        isErrorValue = emailState.showErrors()
    )

    emailState.getError()?.let { error -> FilledTextFieldError(textError = error) }
}

@Composable
fun Password(label: String, passwordState: FilledTextFieldState) {
    val showPassword = state { false }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.body2,
        label = {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(asset = Icons.Filled.Visibility)
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(asset = Icons.Filled.VisibilityOff)
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isErrorValue = passwordState.showErrors()
    )

    passwordState.getError()?.let { error -> FilledTextFieldError(textError = error) }
}

/**
 * To be removed when [FilledTextField]s support error
 */
@Composable
fun FilledTextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.preferredWidth(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = currentTextStyle().copy(color = MaterialTheme.colors.error)
        )
    }
}

@Composable
fun OrSignInAsGuest(
    onSignedInAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalGravity = Alignment.CenterHorizontally
    ) {
        Surface {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(
                    text = stringResource(id = R.string.or),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
        OutlinedButton(
            onClick = { onSignedInAsGuest() },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp)
        ) {
            Text(text = stringResource(id = R.string.sign_in_guest))
        }
    }
}

@Preview
@Composable
fun SignInSignUpScreenPreview() {
    SignInSignUpScreen(
        topAppBarText = "Preview",
        onSignedInAsGuest = {},
        onBackPressed = {},
        content = {}
    )
}
