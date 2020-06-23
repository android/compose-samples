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
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.animation.animate
import androidx.ui.core.Alignment
import androidx.ui.core.Constraints
import androidx.ui.core.Constraints.Companion.Infinity
import androidx.ui.core.DensityAmbient
import androidx.ui.core.LayoutDirection
import androidx.ui.core.LayoutModifier
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import androidx.ui.core.boundsInParent
import androidx.ui.core.onPositioned
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.offsetPx
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.wrapContentHeight
import androidx.ui.material.Button
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.min
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme

sealed class WelcomeEvent {
    data class SignInSignUp(val email: String) : WelcomeEvent()
    object SignInAsGuest : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit) {
    var brandingBottom by state { 0f }
    var showBranding by state { true }
    var heightWithBranding by state { 0 }

    val currentOffsetHolder = state { 0f }
    currentOffsetHolder.value = animate(
        if (showBranding) 0f else -brandingBottom
    )
    val heightDp = with(DensityAmbient.current) { heightWithBranding.toDp() }
    Column(
        modifier = Modifier.fillMaxWidth()
            .brandingPreferredHeight(showBranding, heightDp)
            .offsetPx(y = currentOffsetHolder)
            .onPositioned {
                if (showBranding) {
                    heightWithBranding = it.size.height
                }
            }
    ) {
        Branding(
            modifier = Modifier.fillMaxWidth().weight(1f).onPositioned {
                if (brandingBottom == 0f) {
                    brandingBottom = it.boundsInParent.bottom
                }
            }
        )
        SignInCreateAccount(
            onEvent = onEvent,
            onFocusChange = { focused -> showBranding = !focused },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
    }
}

private fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        Modifier
            .noHeightConstraints()
            .preferredHeight(heightDp)
    } else {
        Modifier
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(modifier = Modifier.gravity(Alignment.CenterHorizontally).padding(horizontal = 76.dp))
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
private fun Logo(
    lightTheme: Boolean = MaterialTheme.colors.isLight,
    modifier: Modifier = Modifier
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        asset = vectorResource(id = assetId),
        modifier = modifier
    )
}

@Composable
private fun SignInCreateAccount(
    onEvent: (WelcomeEvent) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalGravity = Alignment.CenterHorizontally) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                text = stringResource(id = R.string.sign_in_create_account),
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        val emailState = remember { EmailState() }
        onFocusChange(emailState.isFocused)
        Email(emailState)

        Button(
            onClick = {
                if (emailState.isValid) {
                    onEvent(WelcomeEvent.SignInSignUp(emailState.text))
                } else {
                    emailState.enableShowErrors()
                }
            },
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

fun Modifier.noHeightConstraints() = this + NoHeightConstraints

/**
 * A modifier that removes any height constraints and positions the wrapped layout at
 * the top of the available space. This should be provided in Compose b/158559319
 */
object NoHeightConstraints : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(
            constraints.copy(
                minHeight = 0,
                maxHeight = Infinity
            )
        )
        return layout(
            placeable.width,
            min(placeable.height.toDp(), constraints.maxHeight.dp).toIntPx()
        ) {
            placeable.place(0, 0)
        }
    }
}

@Preview(name = "Welcome light theme")
@Composable
fun WelcomeScreenPreview() {
    JetsurveyTheme {
        WelcomeScreen {}
    }
}

@Preview(name = "Welcome dark theme")
@Composable
fun WelcomeScreenPreviewDark() {
    JetsurveyTheme(darkTheme = true) {
        WelcomeScreen {}
    }
}
