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

package com.example.compose.jetchat.theme

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val Yellow400 = Color(0xFFF6E547)
private val Yellow600 = Color(0xFFF5CF1B)
private val Yellow700 = Color(0xFFF3B711)
private val Yellow800 = Color(0xFFF29F05)

private val Blue500 = Color(0xFF0540F2)
private val Blue800 = Color(0xFF001CCF)
private val Blue300 = Color(0xFF6F7EF9)
private val Blue400 = Color(0xFF4860F7)

private val Red300 = Color(0xFFEA6D7E)
private val Red800 = Color(0xFFD00036)

private val JetchatDarkPalette = darkColorPalette(
    primary = Blue300,
    primaryVariant = Blue400,
    onPrimary = Color.White,
    secondary = Yellow400,
    onSecondary = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White,
    error = Red300,
    onError = Color.Black
)

private val JetchatLightPalette = lightColorPalette(
    primary = Blue500,
    primaryVariant = Blue800,
    onPrimary = Color.White,
    secondary = Yellow700,
    secondaryVariant = Yellow800,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
    error = Red800,
    onError = Color.White
)

@Composable
fun JetchatTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: ColorPalette? = null,
    content: @Composable() () -> Unit
) {
    val myColors = colors ?: if (isDarkTheme) JetchatDarkPalette else JetchatLightPalette

    MaterialTheme(colors = myColors, content = content, typography = JetchatTypography)
}
