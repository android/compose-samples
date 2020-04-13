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

package com.example.compose.jetchat

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import androidx.ui.text.font.FontFamily


private val yellow200 = Color(0xffffeb46)
private val yellow400 = Color(0xffffc000)
private val yellow500 = Color(0xffffde03)
private val yellowDarkPrimary = Color(0xff242316)

private val blue200 = Color(0xff91a4fc)
private val blue700 = Color(0xff0336ff)
private val blue800 = Color(0xff0035c9)

private val pink200 = Color(0xffff7597)
private val pink500 = Color(0xffff0266)
private val pinkDarkPrimary = Color(0xff24191c)

private val appFontFamily = FontFamily.Default

private val myTypography = Typography(
    defaultFontFamily = appFontFamily
)

@Composable
fun JetChatProfileTheme(
    userIsMe: Boolean,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    if (userIsMe) {
        JetChatProfileMeTheme(isDarkTheme, content = content)
    } else {
        JetChatProfileOtherTheme(isDarkTheme, content = content)
    }
}

@Composable
private fun JetChatProfileMeTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val profileMeColors = if (isDarkTheme) {
        darkColorPalette(
            primary = yellow200,
            secondary = blue200,
            onSecondary = Color.Black,
            surface = yellowDarkPrimary
        )
    } else {
        lightColorPalette(
            primary = yellow500,
            primaryVariant = yellow400,
            onPrimary = Color.Black,
            secondary = blue700,
            secondaryVariant = blue800,
            onSecondary = Color.White
        )
    }

    JetChatTheme(colors = profileMeColors, content = content)
}

@Composable
private fun JetChatProfileOtherTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val profileMeColors = if (isDarkTheme) {
        darkColorPalette(
            primary = pink200,
            secondary = blue200,
            onSecondary = Color.Black,
            surface = pinkDarkPrimary
        )
    } else {
        lightColorPalette(
            primary = pink500,
            primaryVariant = pink500,
            onPrimary = Color.Black,
            secondary = blue700,
            secondaryVariant = blue800,
            onSecondary = Color.White
        )
    }

    JetChatTheme(colors = profileMeColors, content = content)
}

@Composable
fun JetChatTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: ColorPalette? = null, content: @Composable() () -> Unit
) {

    val myColors = colors ?: if (isDarkTheme) {
        darkColorPalette(
            primary = yellow200,
            secondary = blue200,
            onSecondary = Color.Black,
            surface = yellowDarkPrimary
        )
    } else {
        lightColorPalette(
            primary = yellow500,
            primaryVariant = yellow400,
            onPrimary = Color.Black,
            secondary = blue700,
            secondaryVariant = blue800,
            onSecondary = Color.White
        )
    }

    MaterialTheme(colors = myColors, content = content, typography = myTypography)
}
