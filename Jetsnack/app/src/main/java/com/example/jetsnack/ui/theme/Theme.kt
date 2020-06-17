/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui.theme

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.StructurallyEqual
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.example.jetsnack.ui.utils.SystemUiControllerAmbient

private val LightColorPalette = JetsnackColorPalette(
    gradient1 = listOf(Shadow2, Ocean3, Shadow4),
    gradient2 = listOf(Shadow4, Ocean3, Shadow2),
    gradient3 = listOf(Rose4, Lavender3, Rose2),
    gradient4 = listOf(Rose2, Lavender3, Rose4),
    materialPalette = lightColorPalette(
        primary = Shadow5,
        primaryVariant = Shadow5,
        secondary = Ocean3,
        error = Color(0xffd00036)
    )
)

private val DarkColorPalette = JetsnackColorPalette(
    gradient1 = listOf(Shadow5, Ocean7, Shadow9),
    gradient2 = listOf(Shadow9, Ocean7, Shadow5),
    gradient3 = listOf(Rose11, Lavender7, Rose8),
    gradient4 = listOf(Rose8, Lavender7, Rose11),
    materialPalette = darkColorPalette(
        primary = Shadow1,
        primaryVariant = Shadow1,
        secondary = Ocean7,
        background = Color.Black,
        surface = Color.Black,
        error = Color(0xffea6d7e)
    )
)

@Composable
fun JetsnackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val sysUiController = SystemUiControllerAmbient.current
    onCommit(sysUiController, colors.background) {
        sysUiController.setSystemBarsColor(
            color = colors.background.copy(alpha = 0.96f)
        )
    }

    ProvideJetsnackColors(colors) {
        MaterialTheme(
            colors = colors,
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
 * "Extend" Material [ColorPalette] with our own theme colors.
 */
@Stable
class JetsnackColorPalette(
    gradient1: List<Color>,
    gradient2: List<Color>,
    gradient3: List<Color>,
    gradient4: List<Color>,
    materialPalette: ColorPalette
) : ColorPalette by materialPalette {
    var gradient1 by mutableStateOf(gradient1, StructurallyEqual)
        private set
    var gradient2 by mutableStateOf(gradient2, StructurallyEqual)
        private set
    var gradient3 by mutableStateOf(gradient3, StructurallyEqual)
        private set
    var gradient4 by mutableStateOf(gradient4, StructurallyEqual)
        private set

    fun update(other: JetsnackColorPalette) {
        gradient1 = other.gradient1
        gradient2 = other.gradient2
        gradient3 = other.gradient3
        gradient4 = other.gradient4
    }
}

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
