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
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Immutable
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.UiMediaScope.ViewingDistance
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.unit.DpOffset
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

/**
 * Creates an elliptical radial gradient brush that emulates the CSS radial-gradient spec.
 *
 * @param colors The colors to be distributed along the gradient.
 * @param stops Optional color stops (0.0 to 1.0).
 * @param radiusXPercent The horizontal radius as a percentage of the width (1.0 = 100%).
 * @param radiusYPercent The vertical radius as a percentage of the height (1.0 = 100%).
 * @param centerXPercent The horizontal center position as a percentage of the width.
 * @param centerYPercent The vertical center position as a percentage of the height.
 * @param tileMode The tile mode for the gradient.
 */
fun ellipticalGradient(
    colors: List<Color>,
    stops: List<Float>? = null,
    radiusXPercent: Float,
    radiusYPercent: Float,
    centerXPercent: Float = 0.5f,
    centerYPercent: Float = 0.5f,
    tileMode: TileMode = TileMode.Clamp,
): ShaderBrush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val rX = size.width * radiusXPercent
        val rY = size.height * radiusYPercent
        val cX = size.width * centerXPercent
        val cY = size.height * centerYPercent

        this.transform = Matrix().apply {
            reset()
            translate(cX, cY)
            scale(rX, rY)
        }

        return RadialGradientShader(
            colors = colors,
            colorStops = stops,
            center = Offset.Zero,
            radius = 1f,
            tileMode = tileMode,
        )
    }
}

@Immutable
data class Styles(
    val buttonStyle: Style = Style {
        shape(shapes.small)
        // todo extract colors from theme
        val schemePrimary = Color(0xFF9685FF)
        val schemeInversePrimary = Color(0xFF9D8EFA)
        val schemeTertiary = Color(0xFFFFC8A4)
        val contentColor = Color(0xff0E0066)
        background(
            ellipticalGradient(
                colors = listOf(schemePrimary, schemeTertiary),
                radiusXPercent = 1.3f,
                radiusYPercent = 0.7232f,
                centerXPercent = 0.4f,
                centerYPercent = 0.55f,
            ),
        )

        contentColor(contentColor)
        if (mediaQuery { keyboardKind == UiMediaScope.KeyboardKind.Physical }) {
            contentPaddingVertical(4.dp)
            contentPaddingHorizontal(8.dp)
            shape(shapes.medium)
        } else {
            contentPaddingVertical(8.dp)
            contentPaddingHorizontal(24.dp)
            shape(shapes.small)
        }
        minSize(58.dp, 48.dp)
        textStyleWithFontFamilyFix(typography.labelLarge)

        dropShadow(Shadow(color = schemePrimary, offset = DpOffset(x = 2.dp, y = 4.dp), radius = 12.dp))
        innerShadow(Shadow(color = schemeInversePrimary, offset = DpOffset(x = (-6).dp, (-4).dp), radius = 4.dp))
        pressed {
            animate {
                background(Brush.radialGradient(listOf(schemePrimary, schemePrimary)))
                dropShadow(Shadow(color = schemePrimary, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = schemeInversePrimary, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
        }
        loading {
            animate {
                background(
                    ellipticalGradient(
                        colors = listOf(schemeTertiary, schemePrimary),
                        radiusXPercent = 1.3f,
                        radiusYPercent = 0.7232f,
                        centerXPercent = 0.4f,
                        centerYPercent = 0.55f,
                    ),
                )
                dropShadow(Shadow(color = schemePrimary, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = schemeInversePrimary, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
        }
        disabled {
            animate {
                background(Color(0xffDDD9D9))
                contentColor(Color(0xFF939090))
                // reset shadow
                dropShadow(Shadow(color = Color.Transparent, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = Color.Transparent, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
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

enum class LoadingState {
    Loading,
    Loaded,
    Error
}

val loadingStateKey = StyleStateKey(LoadingState.Loaded)

// Extension Function on MutableStyleState to query and set the current playState
var MutableStyleState.loadingState
    get() = this[loadingStateKey]
    set(value) {
        this[loadingStateKey] = value
    }

fun StyleScope.loading(value: Style) {
    state(loadingStateKey, value, { key, state -> state[key] == LoadingState.Loading })
}

fun StyleScope.loaded(value: Style) {
    state(loadingStateKey, value, { key, state -> state[key] == LoadingState.Loaded })
}

fun StyleScope.error(value: Style) {
    state(loadingStateKey, value, { key, state -> state[key] == LoadingState.Error })
}