/*
 * Copyright 2020 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.util

import androidx.compose.Composable
import androidx.ui.core.Constraints
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Canvas
import androidx.ui.geometry.Offset
import androidx.ui.geometry.Size
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredSizeIn
import kotlin.math.min

@Composable
fun Circle(constraints: Constraints, color: Color) {
    Canvas(modifier = constraints.toCanvasModifier()) {
        drawCircle(
            center = Offset(size.width / 2, size.height / 2),
            radius = min(size.height, size.width) / 2,
            color = color
        )
    }
}

@Composable
fun SemiRect(constraints: Constraints, color: Color, lookingLeft: Boolean = true) {
    Canvas(modifier = constraints.toCanvasModifier()) {
        val offset = if (lookingLeft) {
            Offset(0f, 0f)
        } else {
            Offset(size.width / 2, 0f)
        }
        val size = Size(width = size.width / 2, height = size.height)

        drawRect(size = size, topLeft = offset, color = color)
    }
}

@Composable
private fun Constraints.toCanvasModifier(): Modifier = with(DensityAmbient.current) {
    Modifier.preferredSizeIn(
        minWidth = minWidth.toDp(),
        minHeight = minHeight.toDp(),
        maxWidth = maxWidth.toDp(),
        maxHeight = maxHeight.toDp()
    ).fillMaxSize()
}
