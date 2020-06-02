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
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
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
import com.example.compose.jetsurvey.isPasswordValid
import com.example.compose.jetsurvey.theme.AppTheme

sealed class SignUpEvent {
    object SignIn : SignUpEvent()
    data class SignUp(val email: String, val password: String) : SignUpEvent()
    object SignInAsGuest : SignUpEvent()
}

@Composable
fun SignUp(onEvent: (SignUpEvent) -> Unit) {
    SignInSignUpScreen(onSignedInAsGuest = { onEvent(SignUpEvent.SignInAsGuest) }) {
        Column {
            SignUpContent(onSignUpSubmitted = { email, password ->
                onEvent(SignUpEvent.SignUp(email, password))
            })
            Spacer(modifier = Modifier.preferredHeight(8.dp))
            TextButton(
                onClick = { onEvent(SignUpEvent.SignIn) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.sign_in))
            }
        }
    }
}

@Composable
fun SignUpContent(onSignUpSubmitted: (email: String, password: String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var emailValidState by state {
            EmailState("", false)
        }
        Email { emailValidState = it }
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        var passwordText by state { "" }
        Password(
            label = stringResource(id = R.string.password),
            onPasswordChanged = { passwordText = it }
        )
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        var passwordConfirmationText by state { "" }
        Password(
            label = stringResource(id = R.string.confirm_password),
            onPasswordChanged = { passwordConfirmationText = it }
        )
        Spacer(modifier = Modifier.preferredHeight(32.dp))

        Button(
            onClick = { onSignUpSubmitted(emailValidState.email, passwordText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailValidState.isValid &&
                    isPasswordValid(passwordText, passwordConfirmationText)
        ) {
            Text(
                text = stringResource(id = R.string.sign_up),
                modifier = Modifier.gravity(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    AppTheme {
        SignUp {}
    }
}
