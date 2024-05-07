/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.theme

import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme
import com.example.jetcaster.designsystem.theme.backgroundDark
import com.example.jetcaster.designsystem.theme.backgroundLight
import com.example.jetcaster.designsystem.theme.errorContainerDark
import com.example.jetcaster.designsystem.theme.errorContainerLight
import com.example.jetcaster.designsystem.theme.errorDark
import com.example.jetcaster.designsystem.theme.errorLight
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseOnSurfaceLight
import com.example.jetcaster.designsystem.theme.inversePrimaryDark
import com.example.jetcaster.designsystem.theme.inversePrimaryLight
import com.example.jetcaster.designsystem.theme.inverseSurfaceDark
import com.example.jetcaster.designsystem.theme.inverseSurfaceLight
import com.example.jetcaster.designsystem.theme.onBackgroundDark
import com.example.jetcaster.designsystem.theme.onBackgroundLight
import com.example.jetcaster.designsystem.theme.onErrorContainerDark
import com.example.jetcaster.designsystem.theme.onErrorContainerLight
import com.example.jetcaster.designsystem.theme.onErrorDark
import com.example.jetcaster.designsystem.theme.onErrorLight
import com.example.jetcaster.designsystem.theme.onPrimaryContainerDark
import com.example.jetcaster.designsystem.theme.onPrimaryContainerLight
import com.example.jetcaster.designsystem.theme.onPrimaryDark
import com.example.jetcaster.designsystem.theme.onPrimaryLight
import com.example.jetcaster.designsystem.theme.onSecondaryContainerDark
import com.example.jetcaster.designsystem.theme.onSecondaryContainerLight
import com.example.jetcaster.designsystem.theme.onSecondaryDark
import com.example.jetcaster.designsystem.theme.onSecondaryLight
import com.example.jetcaster.designsystem.theme.onSurfaceDark
import com.example.jetcaster.designsystem.theme.onSurfaceLight
import com.example.jetcaster.designsystem.theme.onSurfaceVariantDark
import com.example.jetcaster.designsystem.theme.onSurfaceVariantLight
import com.example.jetcaster.designsystem.theme.onTertiaryContainerDark
import com.example.jetcaster.designsystem.theme.onTertiaryContainerLight
import com.example.jetcaster.designsystem.theme.onTertiaryDark
import com.example.jetcaster.designsystem.theme.onTertiaryLight
import com.example.jetcaster.designsystem.theme.outlineDark
import com.example.jetcaster.designsystem.theme.outlineLight
import com.example.jetcaster.designsystem.theme.outlineVariantDark
import com.example.jetcaster.designsystem.theme.outlineVariantLight
import com.example.jetcaster.designsystem.theme.primaryContainerDark
import com.example.jetcaster.designsystem.theme.primaryContainerLight
import com.example.jetcaster.designsystem.theme.primaryDark
import com.example.jetcaster.designsystem.theme.primaryLight
import com.example.jetcaster.designsystem.theme.scrimDark
import com.example.jetcaster.designsystem.theme.scrimLight
import com.example.jetcaster.designsystem.theme.secondaryContainerDark
import com.example.jetcaster.designsystem.theme.secondaryContainerLight
import com.example.jetcaster.designsystem.theme.secondaryDark
import com.example.jetcaster.designsystem.theme.secondaryLight
import com.example.jetcaster.designsystem.theme.surfaceDark
import com.example.jetcaster.designsystem.theme.surfaceLight
import com.example.jetcaster.designsystem.theme.surfaceVariantDark
import com.example.jetcaster.designsystem.theme.surfaceVariantLight
import com.example.jetcaster.designsystem.theme.tertiaryContainerDark
import com.example.jetcaster.designsystem.theme.tertiaryContainerLight
import com.example.jetcaster.designsystem.theme.tertiaryDark
import com.example.jetcaster.designsystem.theme.tertiaryLight

val colorSchemeForDarkMode = darkColorScheme(
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
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    border = outlineDark,
    borderVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
)

// Todo: specify surfaceTint
val colorSchemeForLightMode = lightColorScheme(
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
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    border = outlineLight,
    borderVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
)
