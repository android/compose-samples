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

package com.example.jetsnack.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode

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
fun Brush.Companion.ellipticalGradient(
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
