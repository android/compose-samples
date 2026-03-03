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

package com.example.compose.jetchat.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

/**
 * Custom layout modifier that positions elements relative to text baseline.
 *
 * This modifier sets the distance between the top of the composable and its first baseline.
 * The bottom of the element aligns with the last baseline of the content.
 *
 * @param heightFromBaseline Distance from the top to the first baseline in density-independent pixels
 *
 * @see baselineHeight
 */

/**
 * BaselineHeightModifier.kt - Custom layout modifier for baseline-based text alignment.
 *
 * This file provides a specialized LayoutModifier that positions text elements
 * based on their baselines, enabling pixel-perfect text alignment across the UI.
 *
 * Key Components:
 * - [BaselineHeightModifier]: LayoutModifier implementation for baseline positioning
 * - [baselineHeight]: Extension function for convenient modifier application
 *
 * Use Cases:
 * - Aligning multiple text elements with consistent baseline spacing
 * - Creating professional typography layouts
 * - Distributing text elements with precise visual alignment
 *
 * Visual Example:
 * ```
 *     _______________
 *     |             |   ↑
 *     |             |   |  heightFromBaseline
 *     |Hello, World!|   ↓
 *     ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * ```
 *
 * @see androidx.compose.ui.layout.LayoutModifier
 * @see androidx.compose.ui.layout.FirstBaseline
 * @see androidx.compose.ui.layout.LastBaseline
 */
data class BaselineHeightModifier(val heightFromBaseline: Dp) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {

        val textPlaceable = measurable.measure(constraints)
        val firstBaseline = textPlaceable[FirstBaseline]
        val lastBaseline = textPlaceable[LastBaseline]

        val height = heightFromBaseline.roundToPx() + lastBaseline - firstBaseline
        return layout(constraints.maxWidth, height) {
            val topY = heightFromBaseline.roundToPx() - firstBaseline
            textPlaceable.place(0, topY)
        }
    }
}
/**
 * Custom layout modifier that positions elements relative to text baseline.
 *
 * This modifier sets the distance between the top of the composable and its first baseline.
 * The bottom of the element aligns with the last baseline of the content.
 *
 * @param heightFromBaseline Distance from the top to the first baseline in density-independent pixels
 *
 * @see baselineHeight
 */
fun Modifier.baselineHeight(heightFromBaseline: Dp): Modifier = this.then(BaselineHeightModifier(heightFromBaseline))
