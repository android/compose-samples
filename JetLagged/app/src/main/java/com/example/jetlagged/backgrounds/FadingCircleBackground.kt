/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.jetlagged.backgrounds

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun FadingCircleBackground(bubbleSize: Dp, color: Color) {
    val alphaAnimation = remember {
        Animatable(0.5f)
    }
    LaunchedEffect(Unit) {
        alphaAnimation.animateTo(
            1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val bubbleSizePx = bubbleSize.toPx()
                val paddingPx = 8.dp.toPx()
                val numberCols = size.width / bubbleSizePx
                val numberRows = size.height / bubbleSizePx

                onDrawBehind {
                    repeat(ceil(numberRows).toInt()) { row ->
                        repeat(ceil(numberCols).toInt()) { col ->
                            val offset = if (row.mod(2) == 0)
                                (bubbleSizePx + paddingPx) / 2f else 0f
                            drawCircle(
                                color.copy(
                                    alpha = color.alpha *
                                        ((row) / numberRows * alphaAnimation.value)
                                ),
                                radius = bubbleSizePx / 2f,
                                center = Offset(
                                    (bubbleSizePx + paddingPx) * col + offset,
                                    (bubbleSizePx + paddingPx) * row
                                )
                            )
                        }
                    }
                }
            }
    )
}

@Preview
@Composable
fun FadingCirclePreview() {
    FadingCircleBackground(bubbleSize = 30.dp, color = Color.Red)
}
