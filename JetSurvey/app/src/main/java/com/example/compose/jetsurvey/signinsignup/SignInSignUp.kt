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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
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
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R

@Composable
fun SignInSignUpScreen(
    onSignedInAsGuest: () -> Unit,
    content: @Composable() () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
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

@Composable
fun Email(emailState: FilledTextFieldState = remember { EmailState() }) {
    FilledTextField(
        value = emailState.text,
        onValueChange = { emailState.text = it },
        onFocusChange = { focused ->
            emailState.onFocusChange(focused)
            if (!focused) {
                emailState.enableShowErrors()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.email)) },
        isErrorValue = emailState.showErrors()
    )

    emailState.getError()?.let { error -> FilledTextFieldError(textError = error) }
}

@Composable
fun Password(label: String, passwordState: FilledTextFieldState) {
    FilledTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        visualTransformation = PasswordVisualTransformation(),
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
