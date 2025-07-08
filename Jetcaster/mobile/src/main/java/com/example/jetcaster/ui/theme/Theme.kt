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

package com.example.jetcaster.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.jetcaster.designsystem.theme.JetcasterShapes
import com.example.jetcaster.designsystem.theme.JetcasterTypography
import com.example.jetcaster.designsystem.theme.backgroundDark
import com.example.jetcaster.designsystem.theme.errorContainerDark
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDark
import com.example.jetcaster.designsystem.theme.inversePrimaryDark
import com.example.jetcaster.designsystem.theme.inverseSurfaceDark
import com.example.jetcaster.designsystem.theme.onBackgroundDark
import com.example.jetcaster.designsystem.theme.onErrorContainerDark
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDark
import com.example.jetcaster.designsystem.theme.onPrimaryDark
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.onSurfaceDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDark
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDark
import com.example.jetcaster.designsystem.theme.onTertiaryDark
import com.example.jetcaster.designsystem.theme.outlineDark
import com.example.jetcaster.designsystem.theme.outlineVariantDark
import com.example.jetcaster.designsystem.theme.primaryContainerDark
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.scrimDark
import com.example.jetcaster.designsystem.theme.secondaryContainerDark
import com.example.jetcaster.designsystem.theme.secondaryDark
import com.example.jetcaster.designsystem.theme.surfaceBrightDark
import com.example.jetcaster.designsystem.theme.surfaceContainerDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDark
import com.example.jetcaster.designsystem.theme.surfaceDark
import com.example.jetcaster.designsystem.theme.surfaceDimDark
import com.example.jetcaster.designsystem.theme.surfaceVariantDark
import com.example.jetcaster.designsystem.theme.tertiaryContainerDark
import com.example.jetcaster.designsystem.theme.tertiaryDark

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JetcasterTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = darkScheme

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        shapes = JetcasterShapes,
        typography = JetcasterTypography,
        content = content,
    )
}
