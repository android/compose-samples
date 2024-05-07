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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import java.time.Duration

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun Seekbar(
    timeElapsed: Duration,
    length: Duration,
    modifier: Modifier = Modifier,
    knobSize: Dp = 8.dp
) {
    val color = SolidColor(MaterialTheme.colorScheme.onSurface)
    Box(
        modifier.drawWithCache {
            onDrawBehind {
                val knobRadius = knobSize.toPx() / 2

                val start = Offset.Zero.copy(y = knobRadius)
                val end = start.copy(x = size.width)

                val knobCenter = start.copy(
                    x = timeElapsed.seconds.toFloat() / length.seconds.toFloat() * size.width
                )

                drawLine(
                    color, start, end,
                )
                drawCircle(color, knobRadius, knobCenter)
            }
        }
    )
}
