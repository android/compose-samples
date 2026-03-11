package com.example.jetsnack.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class AppStyles(
    val buttonStyle: Style = Style {
        val colors = colors
        
        contentPadding(24.dp, 8.dp) 
        minWidth(58.dp) 
        minHeight(40.dp) 

        background(Brush.horizontalGradient(colors.interactivePrimary))
        shape(RoundedCornerShape(percent = 50))
        clip()
        
        contentColor(colors.textInteractive)
        
        disabled {
            animate {
                background(Brush.horizontalGradient(colors.interactiveSecondary))
                contentColor(colors.textHelp)
            }
        }
    },
    val surfaceStyle: Style = Style {
        val colors = colors
        background(colors.uiBackground)
        contentColor(colors.textSecondary)
        clip()
    },
    val cardStyle: Style = Style {
        val colors = colors
        background(colors.uiBackground)
        contentColor(colors.textPrimary)
        clip()
    },
    val dividerStyle: Style = Style {
        val colors = colors
        background(colors.uiBorder.copy(alpha = 0.12f))
        height(1.dp)
        fillWidth()
    },
)

val LocalAppStyles = staticCompositionLocalOf { AppStyles() }
