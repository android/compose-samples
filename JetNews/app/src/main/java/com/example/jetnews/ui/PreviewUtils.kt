package com.example.jetnews.ui

import androidx.compose.Composable
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.surface.Surface

@Composable
internal fun themedPreview(
    colors: ColorPalette = lightThemeColors,
    typography: Typography = themeTypography,
    children: @Composable() () -> Unit
) {
    MaterialTheme(colors = colors, typography = typography) {
        Surface {
            children()
        }
    }
}
