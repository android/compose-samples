package com.rs.crypto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorPalette = lightColors(
    primary = Primary,
    secondary = Secondary
)

object Theme {
    val paddingSizeValue = 24.dp
    val radiusSizeValue = 12.dp
    val fontSizeValue = 14.dp
    val baseSizeValue = 8.dp
}

@Composable
fun CryptocurrencyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}