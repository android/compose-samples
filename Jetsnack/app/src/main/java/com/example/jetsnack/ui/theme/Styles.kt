/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMediaQueryApi::class)

package com.example.jetsnack.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Immutable
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.UiMediaScope.ViewingDistance
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.textStyleWithFontFamilyFix

fun StyleScope.adaptiveFontSize(fontSize: TextUnit) {
    var scaleFactor = when (LocalUiMediaScope.currentValue.viewingDistance) {
        ViewingDistance.Near -> 1f
        ViewingDistance.Medium -> 1.72f
        ViewingDistance.Far -> 1.5f
        else -> 1f
    }
    scaleFactor = when (LocalUiMediaScope.currentValue.pointerPrecision) {
        UiMediaScope.PointerPrecision.Coarse -> scaleFactor * 1f
        UiMediaScope.PointerPrecision.Blunt -> scaleFactor * 0.66f
        UiMediaScope.PointerPrecision.Fine -> scaleFactor * 1f
        UiMediaScope.PointerPrecision.None -> scaleFactor
        else -> {
            scaleFactor
        }
    }
    fontSize(fontSize * scaleFactor)
}

@Immutable
data class Styles(
    val buttonStyle: Style = Style {
        shape(shapes.small)
        background(Brush.linearGradient(colors.interactivePrimary))
        contentColor(colors.textInteractive)
        contentPaddingVertical(8.dp)
        contentPaddingHorizontal(24.dp)
        minSize(58.dp, 48.dp)
        textStyleWithFontFamilyFix(typography.labelLarge)
        disabled {
            animate {
                background(Brush.linearGradient(colors.interactiveSecondary))
                contentColor(colors.textHelp)
            }
        }
    },
    val cardStyle: Style = Style {
        shape(shapes.medium)
        background(colors.uiBackground)
        contentColor(colors.textPrimary)
        /*
        todo elevation
        elevation: Dp = 4.dp,*/
    },
    val dividerStyle: Style = Style {
        background(colors.uiBorder.copy(alpha = 0.12f))
        height(1.dp)
        fillWidth()
    },
    val gradientIconButtonStyle: Style = Style {
        shape(CircleShape)
        clip(true)
        border(2.dp, Brush.linearGradient(colors.interactiveSecondary))
        background(colors.uiBackground)
        pressed {
            animate {
                background(
                    Brush.horizontalGradient(
                        // this was a parameter input into the function? might want to make helper function for it
                        colors = colors.interactiveSecondary,
                        startX = 0f,
                        endX = 200f,
                        tileMode = TileMode.Mirror,
                    ),
                )
            }
        }
    },
    val filterChipStyle: Style = Style {
        shape(shapes.small)
        background(colors.uiBackground)
        contentColor(colors.textSecondary)
        border(1.dp, Brush.linearGradient(colors.interactiveSecondary))
        // todo elevation = 2.dp,
        selected {
            animate {
                background(colors.brandSecondary)
                contentColor(Color.Black)
                border(1.dp, Color.Transparent)
            }
        }
    },
    val defaultTextStyle: Style = Style {
        textStyleWithFontFamilyFix(LocalTextStyle.currentValue)
    },
    val surfaceStyle: Style = Style {
        shape(RectangleShape)
        background(colors.uiBackground)
        contentColor(colors.textSecondary)
        clip(true)
    },
)
