/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.util

import androidx.annotation.FloatRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Draws a vertical gradient scrim in the foreground.
 *
 * @param color The color of the gradient scrim.
 * @param startYPercentage The start y value, in percentage of the layout's height (0f to 1f)
 * @param endYPercentage The end y value, in percentage of the layout's height (0f to 1f). This
 * value can be smaller than [startYPercentage]. If that is the case, then the gradient direction
 * will reverse (decaying downwards, instead of decaying upwards).
 * @param decay The exponential decay to apply to the gradient. Defaults to `1.0f` which is
 * a linear gradient.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in
 * the higher visual quality at the cost of draw performance. Defaults to `16`.
 */
fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16
): Modifier = composed {
    val colors = remember(color, numStops) {
        if (decay != 1f) {
            // If we have a non-linear decay, we need to create the color gradient steps
            // manually
            val baseAlpha = color.alpha
            List(numStops) { i ->
                val x = i * 1f / (numStops - 1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // If we have a linear decay, we just create a simple list of start + end colors
            listOf(color.copy(alpha = 0f), color)
        }
    }

    val brush = remember(colors, startYPercentage, endYPercentage) {
        // Reverse the gradient if decaying downwards
        Brush.verticalGradient(
            colors = if (startYPercentage < endYPercentage) colors else colors.reversed(),
        )
    }

    drawBehind {
        // Calculate the topLeft and bottomRight with the invariant that topLeft is actually above
        // and left of bottomRight
        val topLeft = Offset(0f, size.height * min(startYPercentage, endYPercentage))
        val bottomRight = Offset(size.width, size.height * max(startYPercentage, endYPercentage))

        drawRect(
            topLeft = topLeft,
            size = Rect(topLeft, bottomRight).size,
            brush = brush
        )
    }
}
