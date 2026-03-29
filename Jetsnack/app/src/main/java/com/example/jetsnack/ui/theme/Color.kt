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

package com.example.jetsnack.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Jetsnack custom Color Palette
 */
@Immutable
data class JetsnackColors(
    val gradient1: List<Color>,
    val gradient2: List<Color>,
    val gradient3: List<Color>,
    val brand: Color,
    val brandLight: Color,
    val brandSecondary: Color,
    val uiBackground: Color,
    val uiBorder: Color,
    val uiFloated: Color,
    val interactivePrimary: List<Color> = gradient2,
    val interactiveSecondary: List<Color> = gradient1,
    val interactiveMask: List<Color> = gradient3,
    val interactiveDisabled: Color,
    val interactiveDisabledText: Color,
    val textPrimary: Color = brand,
    val textSecondary: Color,
    val textHelp: Color,
    val textInteractive: Color,
    val textLink: Color,
    val iconPrimary: Color = brand,
    val iconSecondary: Color,
    val iconInteractive: Color,
    val iconInteractiveInactive: Color,
    val error: Color,
    val notificationBadge: Color = error,
    val cardHighlightBackground: Color,
    val cardHighlightBorder: Color,
    val isDark: Boolean,
)
/**
 * A Material [Colors] implementation which sets all colors to [debugColor] to discourage usage of
 * [MaterialTheme.colorScheme] in preference to [JetsnackTheme.colors].
 */
fun debugColors(darkTheme: Boolean, debugColor: Color = Color.Magenta) = ColorScheme(
    primary = debugColor,
    onPrimary = debugColor,
    primaryContainer = debugColor,
    onPrimaryContainer = debugColor,
    inversePrimary = debugColor,
    secondary = debugColor,
    onSecondary = debugColor,
    secondaryContainer = debugColor,
    onSecondaryContainer = debugColor,
    tertiary = debugColor,
    onTertiary = debugColor,
    tertiaryContainer = debugColor,
    onTertiaryContainer = debugColor,
    background = debugColor,
    onBackground = debugColor,
    surface = debugColor,
    onSurface = debugColor,
    surfaceVariant = debugColor,
    onSurfaceVariant = debugColor,
    surfaceTint = debugColor,
    inverseSurface = debugColor,
    inverseOnSurface = debugColor,
    error = debugColor,
    onError = debugColor,
    errorContainer = debugColor,
    onErrorContainer = debugColor,
    outline = debugColor,
    outlineVariant = debugColor,
    scrim = debugColor,
    surfaceBright = debugColor,
    surfaceDim = debugColor,
    surfaceContainer = debugColor,
    surfaceContainerHigh = debugColor,
    surfaceContainerHighest = debugColor,
    surfaceContainerLow = debugColor,
    surfaceContainerLowest = debugColor,
    primaryFixed = debugColor,
    primaryFixedDim = debugColor,
    onPrimaryFixed = debugColor,
    onPrimaryFixedVariant = debugColor,
    secondaryFixed = debugColor,
    secondaryFixedDim = debugColor,
    onSecondaryFixed = debugColor,
    onSecondaryFixedVariant = debugColor,
    tertiaryFixed = debugColor,
    tertiaryFixedDim = debugColor,
    onTertiaryFixed = debugColor,
    onTertiaryFixedVariant = debugColor,
)

val Neutral8 = Color(0xff121212)
val Neutral7 = Color(0xde000000)
val Neutral6 = Color(0x99000000)
val Neutral5 = Color(0x61000000)
val Neutral4 = Color(0x1f000000)
val Neutral3 = Color(0x1fffffff)
val Neutral2 = Color(0x61ffffff)
val Neutral1 = Color(0xbdffffff)
val Neutral0 = Color(0xffffffff)
val FunctionalGrey = Color(0xfff6f6f6)
val FunctionalDarkGrey = Color(0xff2e2e2e)

const val AlphaNearOpaque = 0.95f

// New Color Palette
val WarpCoreLight = Color(0xFF9685FF)
val WarpCoreDark = Color(0xFF311EA9)
val NebulaLight = Color(0xFFE4E1FA)
val NebulaDark = Color(0xFF3229CD)
val VastOfNightLight = Color(0xFF0E0066)
val VastOfNightDark = Color(0xFFFDFCFC)
val ScienceLight = Color(0xFF8BDEBE)
val ScienceDark = Color(0xFF297258)
val TricorderLight = Color(0xFFBEFBE3)
val TricorderDark = Color(0xFF1EBE7F)
val DeepSpaceLight = Color(0xFF005C5E)
val DeepSpaceDark = Color(0xFFA4F1F2)
val StarburstLight = Color(0xFFFFC8A4)
val StarburstDark = Color(0xFFCC9570)
val TeleportLight = Color(0xFFFFECE9)
val TeleportDark = Color(0xFFD16A5A)
val SalamanderLight = Color(0xFF544337)
val SalamanderDark = Color(0xFFF1DED1)
val RedShirtLight = Color(0xFFFFDAD6)
val RedShirtDark = Color(0xFF93000A)
val CommandLight = Color(0xFF93000A)
val CommandDark = Color(0xFFFFDAD6)
val CloudLight = Color(0xFFFCF8F9)
val CloudDark = Color(0xFF141314)
val ShuttleLight = Color(0xFF222B2B)
val ShuttleDark = Color(0xFFE3E8E8)
val OutlineLight = Color(0xFF7F7573)
val OutlineDark = Color(0xFF9A8E8C)
val OutlineVariantLight = Color(0xFFD1C4C1)
val OutlineVariantDark = Color(0xFF4E4543)
val Warp10Light = Color(0xFFFFFFFF)
val Warp10Dark = Color(0xFF0E0E0F)
val Warp5Light = Color(0xFFF7F3F3)
val Warp5Dark = Color(0xFF1C1B1C)
val WarpLight = Color(0xFFEBE7E7)
val WarpDark = Color(0xFF2A2A2A)

val gradient1Light = listOf(NebulaLight, ScienceLight, DeepSpaceLight)
val gradient1Dark = listOf(NebulaDark, ScienceDark, DeepSpaceDark)
val gradient2Light = listOf(WarpCoreLight, StarburstLight)
val gradient2Dark = listOf(WarpCoreDark, StarburstDark)
val gradient3Light = listOf(NebulaLight, WarpCoreLight, VastOfNightLight)
val gradient3Dark = listOf(NebulaDark, WarpCoreDark, VastOfNightDark)

internal val LightColorPalette = JetsnackColors(
    brand = WarpCoreLight,
    brandLight = NebulaLight,
    brandSecondary = ScienceLight,
    uiBackground = CloudLight,
    uiBorder = OutlineLight,
    uiFloated = WarpLight,
    textSecondary = VastOfNightLight,
    textHelp = ShuttleLight,
    textInteractive = ShuttleLight,
    textPrimary = DeepSpaceLight,
    textLink = DeepSpaceLight,
    iconPrimary = ShuttleLight,
    iconSecondary = ShuttleLight,
    iconInteractive = WarpCoreLight,
    iconInteractiveInactive = ShuttleLight,
    interactiveDisabled = WarpLight,
    interactiveDisabledText = ShuttleLight,
    error = CommandLight,
    gradient1 = gradient1Light,
    gradient2 = gradient2Light,
    gradient3 = gradient3Light,
    cardHighlightBackground = TeleportLight,
    cardHighlightBorder = StarburstLight,
    isDark = false,
)

internal val DarkColorPalette = JetsnackColors(
    brand = WarpCoreDark,
    brandLight = NebulaDark,
    brandSecondary = ScienceDark,
    uiBackground = CloudDark,
    uiBorder = OutlineDark,
    uiFloated = WarpDark,
    textPrimary = DeepSpaceDark,
    textSecondary = VastOfNightDark,
    textHelp = ShuttleDark,
    textInteractive = ShuttleDark,
    textLink = DeepSpaceDark,
    iconPrimary = ShuttleDark,
    iconSecondary = ShuttleDark,
    iconInteractive = WarpCoreDark,
    iconInteractiveInactive = ShuttleDark,
    interactiveDisabled = WarpDark,
    interactiveDisabledText = ShuttleDark,
    error = CommandDark,
    gradient1 = gradient1Dark,
    gradient2 = gradient2Dark,
    gradient3 = gradient3Dark,
    cardHighlightBackground = TeleportDark,
    cardHighlightBorder = StarburstDark,
    isDark = true,
)
