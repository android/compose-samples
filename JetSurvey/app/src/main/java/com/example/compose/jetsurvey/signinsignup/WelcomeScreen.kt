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
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Button
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme

sealed class WelcomeEvent {
    data class SignInSignUp(val email: String) : WelcomeEvent()
    object SignInAsGuest : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Branding(
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        SignInCreateAccount(
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Image(
            asset = vectorResource(id = R.drawable.ic_logo_group),
            modifier = Modifier.gravity(Alignment.CenterHorizontally).padding(horizontal = 76.dp)
        )
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SignInCreateAccount(
    onEvent: (WelcomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalGravity = Alignment.CenterHorizontally) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                text = stringResource(id = R.string.sign_in_create_account),
                style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.W500),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        val emailState = remember { EmailState() }
        Email(emailState)
        Button(
            onClick = { onEvent(WelcomeEvent.SignInSignUp(emailState.text)) },
            enabled = emailState.isValid,
            modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp)
        ) {
            Text(
                text = stringResource(id = R.string.user_continue),
                style = MaterialTheme.typography.subtitle2
            )
        }
        OrSignInAsGuest(
            onSignedInAsGuest = { onEvent(WelcomeEvent.SignInAsGuest) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    JetsurveyTheme {
        WelcomeScreen {}
    }
}
