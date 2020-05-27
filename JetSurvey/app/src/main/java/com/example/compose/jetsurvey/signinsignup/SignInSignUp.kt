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
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.currentTextStyle
import androidx.ui.input.PasswordVisualTransformation
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidth
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.emailValidationError
import com.example.compose.jetsurvey.isEmailValid

@Composable
fun SignInSignUpScreen(
    onSignedInAsGuest: () -> Unit,
    content: @Composable() () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Logo(
            modifier = Modifier.weight(0.3f).gravity(Alignment.CenterHorizontally)
        )
        Column(modifier = Modifier.weight(0.7f).padding(24.dp)) {
            Box(modifier = Modifier.weight(0.1f)) {
                content()
            }
            TextButton(
                onClick = { onSignedInAsGuest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.sign_in_guest))
            }
        }
    }
}

data class EmailState(val email: String, val isValid: Boolean)

@Composable
fun Email(onEmailChanged: (EmailState) -> Unit) {
    val (hasLostFocus, updateHasLostFocus) = state { false }
    val (emailText, updateEmailText) = state { TextFieldValue() }
    FilledTextField(
        value = emailText,
        onValueChange = {
            updateEmailText(it)
            onEmailChanged(
                EmailState(email = it.text, isValid = isEmailValid(it.text))
            )
        },
        onFocusChange = { focused ->
            if (!focused) {
                updateHasLostFocus(true)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.email)) }
    )

    if (hasLostFocus && !isEmailValid(emailText.text)) {
        FilledTextFieldError(
            textError = emailValidationError(emailText.text)
        )
    }
}

@Composable
fun Password(label: String, onPasswordChanged: (String) -> Unit) {
    val (passwordTextState, updatePasswordState) = state { "" }
    FilledTextField(
        value = passwordTextState,
        onValueChange = {
            updatePasswordState(it)
            onPasswordChanged(it)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        visualTransformation = PasswordVisualTransformation()
    )
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
fun Logo(modifier: Modifier = Modifier) {
    Image(
        asset = vectorResource(id = R.drawable.ic_logo_group),
        modifier = modifier
    )
}