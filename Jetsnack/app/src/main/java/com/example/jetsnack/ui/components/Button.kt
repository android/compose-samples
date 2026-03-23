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

@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMediaQueryApi::class)

package com.example.jetsnack.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.LoadingState
import com.example.jetsnack.ui.theme.loadingState
import com.example.jetsnack.ui.utils.DesktopPreviewWrapper
import com.example.jetsnack.ui.utils.PhoneUiMediaScopeWrapper
import com.example.jetsnack.ui.utils.UiMediaScopeWrapper

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    loadingState: LoadingState = LoadingState.Loaded,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
        it.loadingState = loadingState
    }
    Row(
        modifier = modifier
            .semantics(
                properties = {
                    role = Role.Button
                },
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .styleable(styleState, JetsnackTheme.styles.buttonStyle, style),
        content = content,
        verticalAlignment = Alignment.CenterVertically,
    )
}

@PreviewWrapperProvider(PhoneUiMediaScopeWrapper::class)
@Preview
@Composable
private fun ButtonPreview() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt)  {
        Button(onClick = {}) {
            Text(text = "Demo")
        }
    }
}

@PreviewWrapperProvider(PhoneUiMediaScopeWrapper::class)
@Preview
@Composable
private fun ButtonPreviewLoading() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt)  {
        Button(
            onClick = {},
            enabled = true,
            loadingState = LoadingState.Loading,
        ) {
            Text(text = "Demo")
        }
    }
}

@PreviewWrapperProvider(PhoneUiMediaScopeWrapper::class)
@Preview
@Composable
private fun ButtonPreviewDisabled() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt)  {
        Button(
            onClick = {},
            enabled = false,
        ) {
            Text(text = "Demo")
        }
    }
}

@PreviewWrapperProvider(DesktopPreviewWrapper::class)
@Preview
@Composable
private fun ButtonDesktopPreview() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Physical, UiMediaScope.PointerPrecision.Fine) {
        Button(
            onClick = {},
        ) {
            Text(text = "Demo")
        }
    }
}
@PreviewWrapperProvider(DesktopPreviewWrapper::class)
@Preview
@Composable
private fun ButtonDesktopPreviewDisabled() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Physical, UiMediaScope.PointerPrecision.Fine) {
        Button(
            onClick = {},
            enabled = false
        ) {
            Text(text = "Demo")
        }
    }
}

@PreviewWrapperProvider(PhoneUiMediaScopeWrapper::class)
@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    UiMediaScopeWrapper(keyboardKind = UiMediaScope.KeyboardKind.Virtual, UiMediaScope.PointerPrecision.Blunt) {
        Button(
            onClick = {},
            style = {
                shape(RectangleShape)
            },
        ) {
            Text(text = "Demo")
        }
    }
}





