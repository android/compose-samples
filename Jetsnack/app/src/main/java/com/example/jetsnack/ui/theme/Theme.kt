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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.jetsnack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf


@Immutable
class JetsnackTheme(
    val colors: JetsnackColors = LightColorPalette,
    val typography: Typography = Typography,
    val appStyles: AppStyles = AppStyles(),
) {
    val shapes: Shapes = _shapes

    companion object {
        val colors: JetsnackColors
            @Composable @ReadOnlyComposable get() = LocalJetsnackTheme.current.colors

        val typography: Typography
            @Composable @ReadOnlyComposable get() = LocalJetsnackTheme.current.typography

        val shapes: Shapes
            @Composable @ReadOnlyComposable get() = LocalJetsnackTheme.current.shapes

        val appStyles: AppStyles
            @Composable @ReadOnlyComposable get() = LocalJetsnackTheme.current.appStyles

        val LocalJetsnackTheme: ProvidableCompositionLocal<JetsnackTheme>
            get() = LocalJetsnackThemeInstance

        private val _shapes = Shapes
    }
}

internal val LocalJetsnackThemeInstance = staticCompositionLocalOf { JetsnackTheme() }

@Composable
fun JetsnackTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    val theme = JetsnackTheme(colors = colors, appStyles = AppStyles())

    CompositionLocalProvider(
        JetsnackTheme.LocalJetsnackTheme provides theme
    ) {
        MaterialTheme(
            colorScheme = debugColors(darkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
