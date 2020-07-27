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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ColorPalette
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import com.example.jetsnack.ui.utils.SystemUiControllerAmbient

private val LightColorPalette = JetsnackColorPalette(
    brand = Shadow5,
    uiBackground = Neutral0,
    uiBorder = Neutral4,
    textSecondary = Neutral7,
    textHelp = Neutral6,
    textInteractive = Neutral0,
    textLink = Ocean11,
    iconSecondary = Neutral7,
    iconInteractive = Neutral0,
    iconInteractiveInactive = Neutral1,
    error = FunctionalRed,
    gradient6_1 = listOf(Shadow4, Ocean3, Shadow2, Ocean3, Shadow4),
    gradient6_2 = listOf(Rose4, Lavender3, Rose2, Lavender3, Rose4),
    gradient3_1 = listOf(Shadow2, Ocean3, Shadow4),
    gradient3_2 = listOf(Rose2, Lavender3, Rose4),
    gradient2_1 = listOf(Shadow4, Shadow11),
    gradient2_2 = listOf(Ocean3, Shadow3),
    isDark = false
)

private val DarkColorPalette = JetsnackColorPalette(
    brand = Shadow5,
    uiBackground = Neutral8,
    uiBorder = Neutral3,
    textPrimary = Shadow1,
    textSecondary = Neutral0,
    textHelp = Neutral1,
    textInteractive = Neutral7,
    textLink = Ocean2,
    iconPrimary = Shadow1,
    iconSecondary = Neutral0,
    iconInteractive = Neutral7,
    iconInteractiveInactive = Neutral6,
    error = FunctionalRedDark,
    gradient6_1 = listOf(Shadow5, Ocean7, Shadow9, Ocean7, Shadow5),
    gradient6_2 = listOf(Rose11, Lavender7, Rose8, Lavender7, Rose11),
    gradient3_1 = listOf(Shadow9, Ocean7, Shadow5),
    gradient3_2 = listOf(Rose8, Lavender7, Rose11),
    gradient2_1 = listOf(Ocean3, Shadow3),
    gradient2_2 = listOf(Ocean7, Shadow7),
    isDark = true
)

@Composable
fun JetsnackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    val sysUiController = SystemUiControllerAmbient.current
    onCommit(sysUiController, colors.uiBackground) {
        sysUiController.setSystemBarsColor(
            color = colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    }

    ProvideJetsnackColors(colors) {
        MaterialTheme(
            colors = ExplosiveColorPalette(!darkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object JetsnackTheme {
    @Composable
    val colors: JetsnackColorPalette
        get() = JetsnackColorAmbient.current
}

/**
 * Jetsnack Color Palette
 */
@Stable
class JetsnackColorPalette(
    gradient6_1: List<Color>,
    gradient6_2: List<Color>,
    gradient3_1: List<Color>,
    gradient3_2: List<Color>,
    gradient2_1: List<Color>,
    gradient2_2: List<Color>,
    brand: Color,
    uiBackground: Color,
    uiBorder: Color,
    interactivePrimary: List<Color> = gradient2_1,
    interactiveSecondary: List<Color> = gradient2_2,
    interactiveMask: List<Color> = gradient6_1,
    textPrimary: Color = brand,
    textSecondary: Color,
    textHelp: Color,
    textInteractive: Color,
    textLink: Color,
    iconPrimary: Color = brand,
    iconSecondary: Color,
    iconInteractive: Color,
    iconInteractiveInactive: Color,
    error: Color,
    notificationBadge: Color = error,
    isDark: Boolean
) {
    var gradient6_1 by mutableStateOf(gradient6_1, structuralEqualityPolicy())
        private set
    var gradient6_2 by mutableStateOf(gradient6_2, structuralEqualityPolicy())
        private set
    var gradient3_1 by mutableStateOf(gradient3_1, structuralEqualityPolicy())
        private set
    var gradient3_2 by mutableStateOf(gradient3_2, structuralEqualityPolicy())
        private set
    var gradient2_1 by mutableStateOf(gradient2_1, structuralEqualityPolicy())
        private set
    var gradient2_2 by mutableStateOf(gradient2_2, structuralEqualityPolicy())
        private set
    var brand by mutableStateOf(brand, structuralEqualityPolicy())
        private set
    var uiBackground by mutableStateOf(uiBackground, structuralEqualityPolicy())
        private set
    var uiBorder by mutableStateOf(uiBorder, structuralEqualityPolicy())
        private set
    var interactivePrimary by mutableStateOf(interactivePrimary, structuralEqualityPolicy())
        private set
    var interactiveSecondary by mutableStateOf(interactiveSecondary, structuralEqualityPolicy())
        private set
    var interactiveMask by mutableStateOf(interactiveMask, structuralEqualityPolicy())
        private set
    var textPrimary by mutableStateOf(textPrimary, structuralEqualityPolicy())
        private set
    var textSecondary by mutableStateOf(textSecondary, structuralEqualityPolicy())
        private set
    var textHelp by mutableStateOf(textHelp, structuralEqualityPolicy())
        private set
    var textInteractive by mutableStateOf(textInteractive, structuralEqualityPolicy())
        private set
    var textLink by mutableStateOf(textLink, structuralEqualityPolicy())
        private set
    var iconPrimary by mutableStateOf(iconPrimary, structuralEqualityPolicy())
        private set
    var iconSecondary by mutableStateOf(iconSecondary, structuralEqualityPolicy())
        private set
    var iconInteractive by mutableStateOf(iconInteractive, structuralEqualityPolicy())
        private set
    var iconInteractiveInactive by mutableStateOf(
        iconInteractiveInactive,
        structuralEqualityPolicy()
    )
        private set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        private set
    var notificationBadge by mutableStateOf(notificationBadge, structuralEqualityPolicy())
        private set
    var isDark by mutableStateOf(isDark, structuralEqualityPolicy())
        private set

    fun update(other: JetsnackColorPalette) {
        gradient6_1 = other.gradient6_1
        gradient6_2 = other.gradient6_2
        gradient3_1 = other.gradient3_1
        gradient3_2 = other.gradient3_2
        gradient2_1 = other.gradient2_1
        gradient2_2 = other.gradient2_2
        brand = other.brand
        uiBackground = other.uiBackground
        uiBorder = other.uiBorder
        interactivePrimary = other.interactivePrimary
        interactiveSecondary = other.interactiveSecondary
        interactiveMask = other.interactiveMask
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        textHelp = other.textHelp
        textInteractive = other.textInteractive
        textLink = other.textLink
        iconPrimary = other.iconPrimary
        iconSecondary = other.iconSecondary
        iconInteractive = other.iconInteractive
        iconInteractiveInactive = other.iconInteractiveInactive
        error = other.error
        notificationBadge = other.notificationBadge
        isDark = other.isDark
    }
}

/**
 * A Material [ColorPalette] implementation which throws if accessed. This prevents direct usage
 * of [MaterialTheme.colors], directing callers to [JetsnackTheme.colors].
 *
 * Note we allow usage of [ColorPalette.isLight] which is used by [ripples][androidx.ui.material.ripple.DefaultRippleTheme]
 */
private class ExplosiveColorPalette(
    override val isLight: Boolean
) : ColorPalette {
    override val background: Color
        get() = error(ErrorMessage)
    override val error: Color
        get() = error(ErrorMessage)
    override val onBackground: Color
        get() = error(ErrorMessage)
    override val onError: Color
        get() = error(ErrorMessage)
    override val onPrimary: Color
        get() = error(ErrorMessage)
    override val onSecondary: Color
        get() = error(ErrorMessage)
    override val onSurface: Color
        get() = error(ErrorMessage)
    override val primary: Color
        get() = error(ErrorMessage)
    override val primaryVariant: Color
        get() = error(ErrorMessage)
    override val secondary: Color
        get() = error(ErrorMessage)
    override val secondaryVariant: Color
        get() = error(ErrorMessage)
    override val surface: Color
        get() = error(ErrorMessage)
}

private const val ErrorMessage =
    "Do not use MaterialTheme.colors directly, use JetsnackTheme.colors"

@Composable
fun ProvideJetsnackColors(
    colors: JetsnackColorPalette,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)
    Providers(JetsnackColorAmbient provides colorPalette, children = content)
}

private val JetsnackColorAmbient = staticAmbientOf<JetsnackColorPalette> {
    error("No JetsnackColorPalette provided")
}
