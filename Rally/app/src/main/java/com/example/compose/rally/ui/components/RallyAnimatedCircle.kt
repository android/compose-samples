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

package com.example.compose.rally.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp

private const val DividerLengthInDegrees = 1.8f
private val AngleOffset = FloatPropKey("angle")
private val Shift = FloatPropKey("shift")

/**
 * A donut chart that animates when loaded.
 */
@Composable
fun AnimatedCircle(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val stroke = with(AmbientDensity.current) { Stroke(5.dp.toPx()) }
    val state = transition(
        definition = CircularTransition,
        initState = AnimatedCircleProgress.START,
        toState = AnimatedCircleProgress.END
    )
    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = state[Shift] - 90f
        proportions.forEachIndexed { index, proportion ->
            val sweep = proportion * state[AngleOffset]
            drawArc(
                color = colors[index],
                startAngle = startAngle + DividerLengthInDegrees / 2,
                sweepAngle = sweep - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
    }
}
private enum class AnimatedCircleProgress { START, END }

private val CircularTransition = transitionDefinition<AnimatedCircleProgress> {
    state(AnimatedCircleProgress.START) {
        this[AngleOffset] = 0f
        this[Shift] = 0f
    }
    state(AnimatedCircleProgress.END) {
        this[AngleOffset] = 360f
        this[Shift] = 30f
    }
    transition(fromState = AnimatedCircleProgress.START, toState = AnimatedCircleProgress.END) {
        AngleOffset using tween(
            delayMillis = 500,
            durationMillis = 900,
            easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
        )
        Shift using tween(
            delayMillis = 500,
            durationMillis = 900,
            easing = LinearOutSlowInEasing
        )
    }
}
