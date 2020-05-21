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
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object SignUp : SignInEvent()
    object SignInAsGuest : SignInEvent()
}

@Composable
fun SignIn(onEvent: (SignInEvent) -> Unit) {
    SignInSignUpScreen(onSignedInAsGuest = { onEvent(SignInEvent.SignInAsGuest) }) {
        Column {
            SignInContent(onSignInSubmitted = { email, password ->
                onEvent(SignInEvent.SignIn(email, password))
            })
            Spacer(modifier = Modifier.preferredHeight(8.dp))
            TextButton(
                onClick = { onEvent(SignInEvent.SignUp) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.sign_up))
            }
        }
    }
}

@Composable
fun SignInContent(onSignInSubmitted: (email: String, password: String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val emailValidState = state {
            EmailState("", false)
        }
        Email(onEmailChanged = { emailState ->
            emailValidState.value = emailState
        })
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        val passwordText = state { "" }
        Password(
            label = stringResource(id = R.string.password),
            onPasswordChanged = { passwordText.value = it }
        )
        Spacer(modifier = Modifier.preferredHeight(32.dp))

        Button(
            onClick = { onSignInSubmitted(emailValidState.value.email, passwordText.value) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailValidState.value.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in),
                modifier = Modifier.gravity(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun SignInPreview() {
    MaterialTheme {
        SignIn({})
    }
}