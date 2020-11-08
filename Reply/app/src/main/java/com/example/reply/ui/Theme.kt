package com.example.reply.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = lightColors(
    primary = blue200,
    primaryVariant = blue300,
    secondary = orange300,
    secondaryVariant = orange300,

    background = black900,
    surface = black800,
    error = red200,

    onPrimary = black900,
    onSecondary = black900,
    onSurface = white50,
    onBackground = white50,

    onError = black900,
)

private val LightColorPalette = lightColors(
    primary = blue700,
    primaryVariant = blue800,
    secondary = orange500,
    onPrimary = white50,
    secondaryVariant = orange400,
    onSecondary = black900,
    onSurface = black900,
    onBackground = black900,
    error = red400,
    onError = black900,
    background = blue50,
    surface = white50
)


@Composable
fun ReplyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = ReplyTypography,
        content = content
    )
}