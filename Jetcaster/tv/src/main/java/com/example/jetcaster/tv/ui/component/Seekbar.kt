/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import java.time.Duration

@Composable
internal fun Seekbar(
    timeElapsed: Duration,
    length: Duration,
    modifier: Modifier = Modifier,
    onMoveLeft: () -> Unit = {},
    onMoveRight: () -> Unit = {},
    knobSize: Dp = 8.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val brush = SolidColor(color)
    val isFocused by interactionSource.collectIsFocusedAsState()
    val outlineSize = knobSize * 1.5f
    Box(
        modifier
            .drawWithCache {
                onDrawBehind {
                    val knobRadius = knobSize.toPx() / 2

                    val start = Offset.Zero.copy(y = knobRadius)
                    val end = start.copy(x = size.width)

                    val knobCenter = start.copy(
                        x = timeElapsed.seconds.toFloat() / length.seconds.toFloat() * size.width
                    )
                    drawLine(
                        brush, start, end,
                    )
                    if (isFocused) {
                        val outlineColor = color.copy(alpha = 0.6f)
                        drawCircle(outlineColor, outlineSize.toPx() / 2, knobCenter)
                    }
                    drawCircle(brush, knobRadius, knobCenter)
                }
            }
            .height(outlineSize)
            .focusable(true, interactionSource)
            .onKeyEvent {
                when {
                    it.type == KeyEventType.KeyUp && it.key == Key.DirectionLeft -> {
                        onMoveLeft()
                        true
                    }

                    it.type == KeyEventType.KeyUp && it.key == Key.DirectionRight -> {
                        onMoveRight()
                        true
                    }

                    else -> false
                }
            }
    )
}
