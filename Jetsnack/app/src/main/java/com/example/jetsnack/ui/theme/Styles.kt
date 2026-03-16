@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.jetsnack.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.jetsnackTextStyle
import com.example.jetsnack.ui.theme.JetsnackTheme.Companion.LocalJetsnackTheme

@Immutable
data class AppStyles(
    val buttonStyle : Style = Style {
        shape( RoundedCornerShape(percent = 50))
        background(Brush.linearGradient(currentJetsnackTheme.colors.interactivePrimary))
        contentColor(currentJetsnackTheme.colors.textInteractive)
        contentPadding( ButtonDefaults.ContentPadding.calculateTopPadding()) // todo file issue to support padding values
        jetsnackTextStyle(currentJetsnackTheme.typography.labelLarge)
        disabled {
            animate {
                background(Brush.linearGradient(currentJetsnackTheme.colors.interactiveSecondary))
                contentColor(currentJetsnackTheme.colors.textHelp)
            }
        }
    },
    val cardStyle: Style = Style {
        shape(currentJetsnackTheme.shapes.medium)
        background(currentJetsnackTheme.colors.uiBackground)
        contentColor(currentJetsnackTheme.colors.textPrimary)
        /*
        todo elevation
        elevation: Dp = 4.dp,*/
    },
    val dividerStyle: Style = Style {
        background(currentJetsnackTheme.colors.uiBorder.copy(alpha = 0.12f))
        height(1.dp)
        fillWidth()
    },
    val gradientIconButtonStyle: Style = Style {
        shape(CircleShape)
        clip(true)
        border(2.dp, Brush.linearGradient(currentJetsnackTheme.colors.interactiveSecondary))
        background(currentJetsnackTheme.colors.uiBackground)
        pressed {
            animate {
                background(Brush.horizontalGradient(
                    // this was a parameter input into the function? might want to make helper function for it
                    colors = currentJetsnackTheme.colors.interactiveSecondary,
                    startX = 0f,
                    endX = 200f,
                    tileMode = TileMode.Mirror,
                ))
            }
        }
    },
    val filterChipStyle: Style = Style {
        shape(currentJetsnackTheme.shapes.small)
        background(currentJetsnackTheme.colors.uiBackground)
        contentColor(currentJetsnackTheme.colors.textSecondary)
        border(1.dp, Brush.linearGradient(currentJetsnackTheme.colors.interactiveSecondary))
        // todo elevation = 2.dp,
        selected {
            animate {
                background(currentJetsnackTheme.colors.brandSecondary)
                contentColor(Color.Black)
                border(1.dp, Color.Transparent)
            }
        }
    },
    val defaultTextStyle: Style = Style {
        jetsnackTextStyle(LocalTextStyle.currentValue)
    },
    val surfaceStyle: Style = Style {
        shape(RectangleShape)
        background(currentJetsnackTheme.colors.uiBackground)
        contentColor(currentJetsnackTheme.colors.textSecondary)
        clip(true)
    }
)
val StyleScope.currentJetsnackTheme: JetsnackTheme
    get() = LocalJetsnackTheme.currentValue