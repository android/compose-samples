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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.jetcaster.designsystem.theme.JetcasterShapes
import com.example.jetcaster.designsystem.theme.JetcasterTypography
import com.example.jetcaster.designsystem.theme.backgroundDark
import com.example.jetcaster.designsystem.theme.backgroundDarkHighContrast
import com.example.jetcaster.designsystem.theme.backgroundDarkMediumContrast
import com.example.jetcaster.designsystem.theme.errorContainerDark
import com.example.jetcaster.designsystem.theme.errorContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.errorContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.errorDarkHighContrast
import com.example.jetcaster.designsystem.theme.errorDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryDark
import com.example.jetcaster.designsystem.theme.inversePrimaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.inversePrimaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.inverseSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onBackgroundDark
import com.example.jetcaster.designsystem.theme.onBackgroundDarkHighContrast
import com.example.jetcaster.designsystem.theme.onBackgroundDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerDark
import com.example.jetcaster.designsystem.theme.onErrorContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onErrorContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onErrorDarkHighContrast
import com.example.jetcaster.designsystem.theme.onErrorDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDark
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onPrimaryDark
import com.example.jetcaster.designsystem.theme.onPrimaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onPrimaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.onSecondaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSecondaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceDark
import com.example.jetcaster.designsystem.theme.onSurfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDark
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.onTertiaryDark
import com.example.jetcaster.designsystem.theme.onTertiaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.onTertiaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.outlineDark
import com.example.jetcaster.designsystem.theme.outlineDarkHighContrast
import com.example.jetcaster.designsystem.theme.outlineDarkMediumContrast
import com.example.jetcaster.designsystem.theme.outlineVariantDark
import com.example.jetcaster.designsystem.theme.outlineVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.outlineVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.primaryContainerDark
import com.example.jetcaster.designsystem.theme.primaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.primaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.primaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.primaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.scrimDark
import com.example.jetcaster.designsystem.theme.scrimDarkHighContrast
import com.example.jetcaster.designsystem.theme.scrimDarkMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerDark
import com.example.jetcaster.designsystem.theme.secondaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.secondaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.secondaryDark
import com.example.jetcaster.designsystem.theme.secondaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.secondaryDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightDark
import com.example.jetcaster.designsystem.theme.surfaceBrightDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceBrightDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerDark
import com.example.jetcaster.designsystem.theme.surfaceContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDark
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerHighestDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDark
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceContainerLowestDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceDark
import com.example.jetcaster.designsystem.theme.surfaceDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceDimDark
import com.example.jetcaster.designsystem.theme.surfaceDimDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceDimDarkMediumContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantDark
import com.example.jetcaster.designsystem.theme.surfaceVariantDarkHighContrast
import com.example.jetcaster.designsystem.theme.surfaceVariantDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerDark
import com.example.jetcaster.designsystem.theme.tertiaryContainerDarkHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryContainerDarkMediumContrast
import com.example.jetcaster.designsystem.theme.tertiaryDark
import com.example.jetcaster.designsystem.theme.tertiaryDarkHighContrast
import com.example.jetcaster.designsystem.theme.tertiaryDarkMediumContrast

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JetcasterTheme(dynamicColor: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> darkScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        shapes = JetcasterShapes,
        typography = JetcasterTypography,
        content = content,
    )
}
