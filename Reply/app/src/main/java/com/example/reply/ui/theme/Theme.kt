package com.example.reply.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

//Material 3 color schemes
private val replyDarkColorScheme = darkColorScheme(
    primary = reply_dark_primary,
    onPrimary = reply_dark_on_primary,
    primaryContainer = reply_dark_primary_container,
    onPrimaryContainer = reply_dark_on_primary_container,
    inversePrimary = reply_dark_primary_inverse,
    secondary = reply_dark_secondary,
    onSecondary = reply_dark_on_secondary,
    secondaryContainer = reply_dark_secondary_container,
    onSecondaryContainer = reply_dark_secondary_container,
    tertiary = reply_dark_tertiary,
    onTertiary = reply_dark_on_tertiary,
    tertiaryContainer = reply_dark_tertiary_container,
    onTertiaryContainer = reply_dark_on_tertiary_container,
    error = reply_dark_error,
    onError = reply_dark_on_error,
    errorContainer = reply_dark_error_container,
    onErrorContainer = reply_dark_on_error_container,
    background = reply_dark_background,
    onBackground = reply_dark_on_background,
    surface = reply_dark_surface,
    onSurface = reply_dark_on_surface,
    inverseSurface = reply_dark_inverse_surface,
    inverseOnSurface = reply_dark_inverse_on_surface,
    surfaceVariant = reply_dark_surface_variant,
    onSurfaceVariant = reply_dark_on_surface_variant,
    outline = reply_dark_outline
)

private val replyLightColorScheme = lightColorScheme(
    primary = reply_light_primary,
    onPrimary = reply_light_on_primary,
    primaryContainer = reply_light_primary_container,
    onPrimaryContainer = reply_light_on_primary_container,
    inversePrimary = reply_light_primary_inverse,
    secondary = reply_light_secondary,
    onSecondary = reply_light_on_secondary,
    secondaryContainer = reply_light_secondary_container,
    onSecondaryContainer = reply_light_secondary_container,
    tertiary = reply_light_tertiary,
    onTertiary = reply_light_on_tertiary,
    tertiaryContainer = reply_light_tertiary_container,
    onTertiaryContainer = reply_light_on_tertiary_container,
    error = reply_light_error,
    onError = reply_light_on_error,
    errorContainer = reply_light_error_container,
    onErrorContainer = reply_light_on_error_container,
    background = reply_light_background,
    onBackground = reply_light_on_background,
    surface = reply_light_surface,
    onSurface = reply_light_on_surface,
    inverseSurface = reply_light_inverse_surface,
    inverseOnSurface = reply_light_inverse_on_surface,
    surfaceVariant = reply_light_surface_variant,
    onSurfaceVariant = reply_light_on_surface_variant,
    outline = reply_light_outline
)

@Composable
fun ReplyM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val selectedColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> replyDarkColorScheme
        else -> replyLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = selectedColorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = selectedColorScheme,
        typography = replyTypography,
        content = content
    )
}