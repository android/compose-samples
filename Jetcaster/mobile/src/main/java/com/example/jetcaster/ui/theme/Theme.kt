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

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.jetcaster.designsystem.theme.JetcasterShapes
import com.example.jetcaster.designsystem.theme.JetcasterTypography
import com.example.jetcaster.designsystem.theme.backgroundDark
import com.example.jetcaster.designsystem.theme.backgroundDarkHighContrast
import com.example.jetcaster.designsystem.theme.backgroundDarkMediumContrast
import com.example.jetcaster.designsystem.theme.backgroundLight
import com.example.jetcaster.designsystem.theme.backgroundLightHighContrast
import com.example.jetcaster.designsystem.theme.backgroundLightMediumContrast
import com.example.jetcaster.designsystem.theme.errorContainerDark
import com.example.jetcaster.designsystem.theme.errorContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.errorContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.errorContainerLight
import com.example.jetcaster.designsystem.theme.errorContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.errorContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.errorDarkHighContrast
import com.example.jetcaster.designsystem.theme.errorDarkMediumContrast
import com.example.jetcaster.designsystem.theme.errorLight
import com.example.jetcaster.designsystem.theme.errorLightHighContrast
import com.example.jetcaster.designsystem.theme.errorLightMediumContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceLight
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceLightHighContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceLightMediumContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryDark
import com.example.jetcaster.designsystem.theme.inversePrimaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryLight
import com.example.jetcaster.designsystem.theme.inversePrimaryLightHighContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceLight
import com.example.jetcaster.designsystem.theme.inverseSurfaceLightHighContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceLightMediumContrast
import com.example.jetcaster.designsystem.theme.onBackgroundDark
import com.example.jetcaster.designsystem.theme.onBackgroundDarkHighContrast
import com.example.jetcaster.designsystem.theme.onBackgroundDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onBackgroundLight
import com.example.jetcaster.designsystem.theme.onBackgroundLightHighContrast
import com.example.jetcaster.designsystem.theme.onBackgroundLightMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerDark
import com.example.jetcaster.designsystem.theme.onErrorContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerLight
import com.example.jetcaster.designsystem.theme.onErrorContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onErrorDarkHighContrast
import com.example.jetcaster.designsystem.theme.onErrorDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorLight
import com.example.jetcaster.designsystem.theme.onErrorLightHighContrast
import com.example.jetcaster.designsystem.theme.onErrorLightMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDark
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerLight
import com.example.jetcaster.designsystem.theme.onPrimaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryDark
import com.example.jetcaster.designsystem.theme.onPrimaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryLight
import com.example.jetcaster.designsystem.theme.onPrimaryLightHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerLight
import com.example.jetcaster.designsystem.theme.onSecondaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.onSecondaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryLight
import com.example.jetcaster.designsystem.theme.onSecondaryLightHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceDark
import com.example.jetcaster.designsystem.theme.onSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceLight
import com.example.jetcaster.designsystem.theme.onSurfaceLightHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceLightMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantLight
import com.example.jetcaster.designsystem.theme.onSurfaceVariantLightHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantLightMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDark
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerLight
import com.example.jetcaster.designsystem.theme.onTertiaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryDark
import com.example.jetcaster.designsystem.theme.onTertiaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryLight
import com.example.jetcaster.designsystem.theme.onTertiaryLightHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.outlineDark
import com.example.jetcaster.designsystem.theme.outlineDarkHighContrast
import com.example.jetcaster.designsystem.theme.outlineDarkMediumContrast
import com.example.jetcaster.designsystem.theme.outlineLight
import com.example.jetcaster.designsystem.theme.outlineLightHighContrast
import com.example.jetcaster.designsystem.theme.outlineLightMediumContrast
import com.example.jetcaster.designsystem.theme.outlineVariantDark
import com.example.jetcaster.designsystem.theme.outlineVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.outlineVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.outlineVariantLight
import com.example.jetcaster.designsystem.theme.outlineVariantLightHighContrast
import com.example.jetcaster.designsystem.theme.outlineVariantLightMediumContrast
import com.example.jetcaster.designsystem.theme.primaryContainerDark
import com.example.jetcaster.designsystem.theme.primaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.primaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.primaryContainerLight
import com.example.jetcaster.designsystem.theme.primaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.primaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.primaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.primaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.primaryLight
import com.example.jetcaster.designsystem.theme.primaryLightHighContrast
import com.example.jetcaster.designsystem.theme.primaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.scrimDark
import com.example.jetcaster.designsystem.theme.scrimDarkHighContrast
import com.example.jetcaster.designsystem.theme.scrimDarkMediumContrast
import com.example.jetcaster.designsystem.theme.scrimLight
import com.example.jetcaster.designsystem.theme.scrimLightHighContrast
import com.example.jetcaster.designsystem.theme.scrimLightMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerDark
import com.example.jetcaster.designsystem.theme.secondaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerLight
import com.example.jetcaster.designsystem.theme.secondaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryDark
import com.example.jetcaster.designsystem.theme.secondaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.secondaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryLight
import com.example.jetcaster.designsystem.theme.secondaryLightHighContrast
import com.example.jetcaster.designsystem.theme.secondaryLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightDark
import com.example.jetcaster.designsystem.theme.surfaceBrightDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightLight
import com.example.jetcaster.designsystem.theme.surfaceBrightLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerDark
import com.example.jetcaster.designsystem.theme.surfaceContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighLight
import com.example.jetcaster.designsystem.theme.surfaceContainerHighLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestLight
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLight
import com.example.jetcaster.designsystem.theme.surfaceContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowLight
import com.example.jetcaster.designsystem.theme.surfaceContainerLowLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestLight
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceDark
import com.example.jetcaster.designsystem.theme.surfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceDimDark
import com.example.jetcaster.designsystem.theme.surfaceDimDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceDimDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceDimLight
import com.example.jetcaster.designsystem.theme.surfaceDimLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceDimLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceLight
import com.example.jetcaster.designsystem.theme.surfaceLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceLightMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantDark
import com.example.jetcaster.designsystem.theme.surfaceVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantLight
import com.example.jetcaster.designsystem.theme.surfaceVariantLightHighContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantLightMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerDark
import com.example.jetcaster.designsystem.theme.tertiaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerLight
import com.example.jetcaster.designsystem.theme.tertiaryContainerLightHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerLightMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryDark
import com.example.jetcaster.designsystem.theme.tertiaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryLight
import com.example.jetcaster.designsystem.theme.tertiaryLightHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

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

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun JetcasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = JetcasterShapes,
        typography = JetcasterTypography,
        content = content
    )
}
