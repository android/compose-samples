@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.jetsnack.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalShapes
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.LocalTypography
import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.jetsnackTextStyle

@Immutable
data class AppStyles(
    val buttonStyle : Style = Style {
        shape( RoundedCornerShape(percent = 50))
        background(Brush.linearGradient(LocalJetsnackColors.currentValue.interactivePrimary))
        contentColor(LocalJetsnackColors.currentValue.textInteractive)
        contentPadding( ButtonDefaults.ContentPadding.calculateTopPadding()) // todo file issue to support padding values
        jetsnackTextStyle(LocalTypography.currentValue.labelLarge)
        disabled {
            animate {
                background(Brush.linearGradient(LocalJetsnackColors.currentValue.interactiveSecondary))
                contentColor(LocalJetsnackColors.currentValue.textHelp)
            }
        }
    },
    val cardStyle: Style = Style {
        shape(LocalShapes.currentValue.medium)
        background(LocalJetsnackColors.currentValue.uiBackground)
        contentColor(LocalJetsnackColors.currentValue.textPrimary)
        /*
        todo elevation
        elevation: Dp = 4.dp,*/
    },
    val dividerStyle: Style = Style {
        background(LocalJetsnackColors.currentValue.uiBorder.copy(alpha = 0.12f))
        height(1.dp)
        fillWidth()
    },
    val gradientIconButtonStyle: Style = Style {
        shape(CircleShape)
        clip(true)
        border(2.dp, Brush.linearGradient(LocalJetsnackColors.currentValue.interactiveSecondary))
        background(LocalJetsnackColors.currentValue.uiBackground)
        pressed {
            animate {
                background(Brush.horizontalGradient(
                    // this was a parameter input into the function? might want to make helper function for it
                    colors = LocalJetsnackColors.currentValue.interactiveSecondary,
                    startX = 0f,
                    endX = 200f,
                    tileMode = TileMode.Mirror,
                ))
            }
        }
    },
    val filterChipStyle: Style = Style {
        shape(LocalShapes.currentValue.small)
        background(LocalJetsnackColors.currentValue.uiBackground)
        contentColor(LocalJetsnackColors.currentValue.textSecondary)
        border(1.dp, Brush.linearGradient(LocalJetsnackColors.currentValue.interactiveSecondary))
        // todo elevation = 2.dp,
        selected {
            animate {
                background(LocalJetsnackColors.currentValue.brandSecondary)
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
        background(LocalJetsnackColors.currentValue.uiBackground)
        contentColor(LocalJetsnackColors.currentValue.textSecondary)
        clip(true)
    }
)
