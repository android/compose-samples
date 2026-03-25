/*
 * Copyright 2026 The Android Open Source Project
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

@file:OptIn(ExperimentalMediaQueryApi::class, ExperimentalComposeUiApi::class)

package com.example.jetsnack.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.LocalNavAnimatedVisibilityScope
import com.example.jetsnack.ui.LocalSharedTransitionScope
import com.example.jetsnack.ui.home.cart.Cart
import com.example.jetsnack.ui.theme.JetsnackTheme

@Composable
fun JetsnackThemeWrapper(content: @Composable () -> Unit) {
    JetsnackTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                CompositionLocalProvider(LocalSharedTransitionScope provides this@SharedTransitionLayout) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@AnimatedVisibility) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun UiMediaScopeWrapper(
    keyboardKind: UiMediaScope.KeyboardKind = UiMediaScope.KeyboardKind.Virtual,
    pointerPrecision: UiMediaScope.PointerPrecision = UiMediaScope.PointerPrecision.Blunt,
    content: @Composable () -> Unit,
) {

    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    BoxWithConstraints {
        val uiMediaScope = object : UiMediaScope {
            override val keyboardKind: UiMediaScope.KeyboardKind
                get() = keyboardKind
            override val windowPosture: UiMediaScope.Posture
                get() = UiMediaScope.Posture.Flat
            override val windowWidth = maxWidth // faking the width and height for previews
            override val windowHeight = maxHeight
            override val pointerPrecision = pointerPrecision
            override val hasMicrophone = true
            override val hasCamera = true
            override val viewingDistance = UiMediaScope.ViewingDistance.Near
        }

        JetsnackTheme {
            CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope) {
                content()
            }
        }
    }
}

// Assuming KeyboardKind is your enum/class
@OptIn(ExperimentalMediaQueryApi::class)
class KeyboardKindProvider : PreviewParameterProvider<UiMediaScope.KeyboardKind> {
    override val values: Sequence<UiMediaScope.KeyboardKind> = sequenceOf(
        UiMediaScope.KeyboardKind.Physical,
        UiMediaScope.KeyboardKind.Virtual,
    )
}
@OptIn(ExperimentalMediaQueryApi::class)
class PointerPrecisionProvider : PreviewParameterProvider<UiMediaScope.PointerPrecision> {
    override val values: Sequence<UiMediaScope.PointerPrecision> = sequenceOf(
        UiMediaScope.PointerPrecision.Fine,
        UiMediaScope.PointerPrecision.Blunt,
        UiMediaScope.PointerPrecision.Coarse,
        UiMediaScope.PointerPrecision.None,
    )
}
