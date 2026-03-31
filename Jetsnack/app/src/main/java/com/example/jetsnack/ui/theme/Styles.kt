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
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.disabled
import androidx.compose.foundation.style.fillHeight
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.selected
import androidx.compose.foundation.style.then
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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.textStyleWithFontFamilyFix
import com.example.jetsnack.ui.utils.ellipticalGradient

@Immutable
data class Styles(
    val buttonStyle: Style = Style {
        shape(shapes.small)
        background(
            Brush.ellipticalGradient(
                colors = colors.interactivePrimary,
                radiusXPercent = 1.3f,
                radiusYPercent = 0.7f,
                centerXPercent = 0.4f,
                centerYPercent = 0.5f,
            ),
        )
        contentColor(colors.textSecondary)
        minWidth(58.dp)
        if (mediaQuery {
                pointerPrecision == UiMediaScope.PointerPrecision.Fine &&
                    keyboardKind == UiMediaScope.KeyboardKind.Physical
            }
        ) {
            contentPaddingVertical(4.dp)
            contentPaddingHorizontal(8.dp)
            shape(shapes.medium)
            minHeight(32.dp)
            textStyleWithFontFamilyFix(typography.labelMedium)
        } else {
            contentPaddingVertical(8.dp)
            contentPaddingHorizontal(24.dp)
            minHeight(40.dp)
            shape(shapes.small)
            textStyleWithFontFamilyFix(typography.labelLarge)
        }

        dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 2.dp), radius = 6.dp))
        innerShadow(Shadow(color = colors.brand.copy(alpha = 0.5f), offset = DpOffset(x = (-6).dp, (-4).dp), radius = 8.dp))

        hovered {
            animate {
                background(colors.brandLight)
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 2.dp), radius = 6.dp))
                innerShadow(Shadow(color = colors.brand.copy(alpha = 0.5f), offset = DpOffset(x = (-6).dp, (-2).dp), radius = 8.dp))
            }
        }
        focused {
            animate {
                border(4.dp, colors.brand)
            }
        }
        pressed {
            animate {
                background(colors.brand)
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = colors.brand, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
            focused {
                animate {
                    border(4.dp, colors.brand)
                }
            }
            hovered {
                // TODO this state is broken - await API changes on animation changes
                // we don't want to combine these two
                // so set the properties to the same
                animate {
                    background(colors.brand)
                    dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                    innerShadow(Shadow(color = colors.brand, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
                }
            }
        }

        loading {
            animate {
                background(
                    Brush.ellipticalGradient(
                        colors = colors.interactivePrimary.reversed(),
                        radiusXPercent = 1.3f,
                        radiusYPercent = 0.7f,
                        centerXPercent = 0.4f,
                        centerYPercent = 0.5f,
                    ),
                )
                dropShadow(Shadow(color = colors.brand, offset = DpOffset(x = 0.dp, y = 0.dp), radius = 0.dp))
                innerShadow(Shadow(color = colors.brand, offset = DpOffset(x = (0).dp, (0).dp), radius = 0.dp))
            }
        }
        disabled {
            animate {
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
        textStyleWithFontFamilyFix(typography.labelSmall)
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
        textStyleWithFontFamilyFix(LocalTextStyle.currentValue)
    },
    val surfaceStyle: Style = Style {
        shape(RectangleShape)
        background(colors.uiBackground)
        contentColor(colors.textSecondary)
        clip(true)
    },
    val baseSnackCardStyle: Style = Style {
        textAlign(TextAlign.Center)

        // todo this animation doesn't seem to play nice
        if (mediaQuery { windowWidth > 500.dp }) {
            animate {
                width(200.dp)
            }
        } else {
            animate {
                width(170.dp)
            }
        }
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
    val highlightGlowCardStyle: Style = baseSnackCardStyle then Style {
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
    val normalCardStyle: Style = baseSnackCardStyle then Style {
        background(Color.Transparent)
        width(100.dp)
        contentPadding(2.dp)
        textAlign(TextAlign.Center)
        pressed {
            background(colors.uiFloated.copy(alpha = 0.5f))
        }
    },
    val plainCardStyle: Style = baseSnackCardStyle then Style {
        background(colors.cardHighlightBackground)
        clip(true)
        border(1.dp, colors.cardHighlightBorder)
    },
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
