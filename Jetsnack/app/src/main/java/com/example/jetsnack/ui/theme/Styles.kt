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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Immutable
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.UiMediaScope.ViewingDistance
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.utils.ellipticalGradient

@Immutable
data class Styles(
    val buttonStyle: Style = Style {
        shape(shapes.small)
        background(Brush.linearGradient(colors = colors.interactivePrimary))
        contentColor(colors.textSecondary)
        minWidth(58.dp)
        textMotion(TextMotion.Animated)
        if (mediaQuery {
                pointerPrecision == UiMediaScope.PointerPrecision.Fine &&
                        keyboardKind == UiMediaScope.KeyboardKind.Physical
            }
        ) {
            contentPaddingVertical(4.dp)
            contentPaddingHorizontal(8.dp)
            shape(shapes.medium)
            minHeight(32.dp)
            textStyle(typography.labelMedium)
        } else {
            contentPaddingVertical(8.dp)
            contentPaddingHorizontal(24.dp)
            minHeight(40.dp)
            shape(shapes.small)
            textStyle(typography.labelLarge)
        }
        fontWeight(FontWeight.Bold)
        dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 2.dp), radius = 6.dp))
        innerShadow(Shadow(color = colors.brand.copy(alpha = 0.5f), offset = DpOffset(x = (0).dp, (0).dp), radius = 8.dp))
        border(4.dp, Color.Transparent)
        hovered {
            animate(tween(1000)) {
                background(colors.brandLight)
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 2.dp), radius = 6.dp))
                innerShadow(Shadow(color = colors.brand.copy(alpha = 0.5f), offset = DpOffset(x = (-6).dp, (-2).dp), radius = 8.dp))
            }
        }
        focused {
            animate(tween(1000)) {
                border(4.dp, colors.brand)
            }
        }
        pressed {
            animate(tween(1000)) {
                scale(1.1f)
                background(colors.brand)
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = colors.brand, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
            focused {
                animate(tween(1000)) {
                    border(4.dp, colors.brand)
                }
            }
        }

        loading {
            animate(tween(1000)) {
                background(colors.loadingBackground)
                // reset shadow
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                // apply purple inner shadow
                innerShadow(Shadow(color = colors.brand, offset = DpOffset(x = (-6).dp, (-2).dp), radius = 8.dp))
            }
        }
        error {
            animate(tween(1000)) {
                background(colors.error)
                contentColor(colors.interactiveDisabled)
                // reset shadow
                dropShadow(Shadow(color = Color.Transparent, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = Color.Transparent, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
        }
        disabled {
            animate(tween(1000)) {
                background(colors.interactiveDisabled)
                contentColor(colors.interactiveDisabledText)
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
        contentColor(colors.textPrimary)
        pressed {
            animate {
                background(
                    Brush.horizontalGradient(
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
        contentColor(colors.textInteractive)
        border(2.dp, Brush.linearGradient(colors.interactiveSecondary))
        minHeight(32.dp)
        textStyle(typography.labelSmall)
        pressed {
            animate {
                val gradient = Brush.ellipticalGradient(
                    colors = colors.interactivePrimary,
                    radiusXPercent = 1.3f,
                    radiusYPercent = 0.7232f,
                    centerXPercent = 0.4f,
                    centerYPercent = 0.55f,
                )
                border(2.dp, gradient)
                background(gradient)
            }
        }
        selected {
            animate {
                background(colors.brand)
                contentColor(colors.textSecondary)
                border(2.dp, colors.brand)
                dropShadow(Shadow(color = colors.brand, radius = 6.dp, offset = DpOffset(0.dp, 2.dp)))
                innerShadow(Shadow(color = colors.brand, offset = DpOffset((-6).dp, (-8).dp), radius = 8.dp))
            }
        }
    },
    val defaultTextStyle: Style = Style {
        // Left empty to allow inherited text properties (like from Button) to cascade.
        // The base material typography fallback is now handled by BasicText's `style` parameter.
    },
    val surfaceStyle: Style = Style {
        shape(RectangleShape)
        background(colors.uiBackground)
        contentColor(colors.textSecondary)
        clip(true)
    },
    val baseSnackCardStyle : Style = Style {
        textAlign(TextAlign.Center)

        hovered {
            animate {
                scale(1.05f)
            }
        }
        focused {
            animate {
                scale(1.05f)
            }
        }
        pressed {
            animate {
                scale(1.05f)
            }
        }
    },
    val responsiveSnackCardStyle : Style = baseSnackCardStyle then Style {
        width(170.dp)

        if (mediaQuery { windowWidth > 500.dp }) {
            width(200.dp)
        }
    },
    val highlightGlowCardStyle : Style = responsiveSnackCardStyle then Style {
        background(colors.brandLight)
        border(0.dp, colors.brandLight)
        hovered {
            animate {
                dropShadow(Shadow(offset = DpOffset(0.dp, 2.dp), radius = 6.dp, color = colors.brand))
                innerShadow(Shadow(offset = DpOffset((-6).dp, (-2).dp), radius = 8.dp, color = colors.brand.copy(alpha = 0.5f)))
            }
        }
        focused {
            animate {
                dropShadow(Shadow(offset = DpOffset(0.dp, 2.dp), radius = 6.dp, color = colors.brand))
                innerShadow(Shadow(offset = DpOffset((-6).dp, (-2).dp), radius = 8.dp, color = colors.brand.copy(alpha = 0.5f)))
            }
        }
    },
    val normalCardStyle : Style = baseSnackCardStyle then Style {
        background(Color.Transparent)
        width(100.dp)
        contentPadding(2.dp)
        textAlign(TextAlign.Center)
        pressed {
            background(colors.uiFloated.copy(alpha = 0.5f))
        }
    },
    val plainCardStyle : Style = responsiveSnackCardStyle then Style {
        background(colors.cardHighlightBackground)
        clip(true)
        border(1.dp, colors.cardHighlightBorder)
    }
)

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

enum class LoadingState {
    Loading,
    Loaded,
    Error,
}

val loadingStateKey = StyleStateKey(LoadingState.Loaded)

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
