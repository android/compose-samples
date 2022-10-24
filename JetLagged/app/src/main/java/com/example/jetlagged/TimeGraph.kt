/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeGraph(
    hoursHeader: @Composable () -> Unit,
    dayItemsCount: Int,
    dayLabel: @Composable (index: Int) -> Unit,
    bar: @Composable TimeGraphScope.(index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayLabels = @Composable { repeat(dayItemsCount) { dayLabel(it) } }
    val bars = @Composable { repeat(dayItemsCount) { TimeGraphScope.bar(it) } }
    Layout(
        contents = listOf(hoursHeader, dayLabels, bars),
        modifier = modifier.padding(bottom = 32.dp)
    ) {
            (hoursHeaderMeasurables, dayLabelMeasurables, barMeasureables),
            constraints,
        ->
        require(hoursHeaderMeasurables.size == 1) {
            "hoursHeader should only emit one composable"
        }
        val hoursHeaderPlaceable = hoursHeaderMeasurables.first().measure(constraints)

        val dayLabelPlaceables = dayLabelMeasurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }

        var totalHeight = hoursHeaderPlaceable.height

        val barPlaceables = barMeasureables.map { measurable ->
            val barParentData = measurable.parentData as TimeGraphParentData
            val barWidth = (barParentData.duration * hoursHeaderPlaceable.width).roundToInt()

            val barPlaceable = measurable.measure(
                constraints.copy(
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
            totalHeight += barPlaceable.height
            barPlaceable
        }

        val totalWidth = dayLabelPlaceables.first().width + hoursHeaderPlaceable.width

        layout(totalWidth, totalHeight) {
            val xPosition = dayLabelPlaceables.first().width
            var yPosition = hoursHeaderPlaceable.height

            hoursHeaderPlaceable.place(xPosition, 0)

            barPlaceables.forEachIndexed { index, barPlaceable ->
                val barParentData = barPlaceable.parentData as TimeGraphParentData
                val barOffset = (barParentData.offset * hoursHeaderPlaceable.width).roundToInt()

                barPlaceable.place(xPosition + barOffset, yPosition)
                // the label depend on the size of the bar content - so should use the same y
                val dayLabelPlaceable = dayLabelPlaceables[index]
                dayLabelPlaceable.place(x = 0, y = yPosition)

                yPosition += barPlaceable.height
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
object TimeGraphScope {
    @Stable
    fun Modifier.timeGraphBar(
        start: LocalDateTime,
        end: LocalDateTime,
        hours: List<Int>,
    ): Modifier {
        val earliestTime = LocalTime.of(hours.first(), 0)
        val durationInHours = ChronoUnit.MINUTES.between(start, end) / 60f
        val durationFromEarliestToStartInHours =
            ChronoUnit.MINUTES.between(earliestTime, start.toLocalTime()) / 60f
        // we add extra half of an hour as hour label text is visually centered in its slot
        val offsetInHours = durationFromEarliestToStartInHours + 0.5f
        return then(
            TimeGraphParentData(
                duration = durationInHours / hours.size,
                offset = offsetInHours / hours.size
            )
        )
    }
}

class TimeGraphParentData(
    val duration: Float,
    val offset: Float,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@TimeGraphParentData
}
