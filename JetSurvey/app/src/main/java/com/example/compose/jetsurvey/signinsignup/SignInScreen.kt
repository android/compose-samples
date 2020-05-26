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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Button
import androidx.ui.material.TextButton
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme

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
        val emailValidState = remember { EmailState() }
        Email(emailValidState)
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        val passwordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState
        )
        Spacer(modifier = Modifier.preferredHeight(32.dp))

        Button(
            onClick = { onSignInSubmitted(emailValidState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailValidState.isValid
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
    JetsurveyTheme {
        SignIn({})
    }
}
