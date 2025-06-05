/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import com.example.jetcaster.designsystem.theme.backgroundDark
import com.example.jetcaster.designsystem.theme.errorContainerDark
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.onBackgroundDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryDark
import com.example.jetcaster.designsystem.theme.outlineDark
import com.example.jetcaster.designsystem.theme.outlineVariantDark
import com.example.jetcaster.designsystem.theme.primaryContainerDark
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.secondaryContainerDark
import com.example.jetcaster.designsystem.theme.secondaryDark
import com.example.jetcaster.designsystem.theme.surfaceContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryDark

internal val wearColorPalette: ColorScheme = ColorScheme(
    primary = primaryDark,
    primaryDim = primaryDark,
    onPrimary = Color(0xFF542104),
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    secondaryDim = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDark,
    onBackground = onBackgroundDarkMediumContrast,
    onSurface = onSurfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDark,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
)
