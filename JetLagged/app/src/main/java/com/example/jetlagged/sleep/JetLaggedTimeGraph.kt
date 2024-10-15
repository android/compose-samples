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

package com.example.jetlagged.sleep

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.ui.theme.JetLaggedTheme
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun JetLaggedSleepGraphCard(
    sleepState: SleepGraphData,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(SleepTab.Week) }

    BasicInformationalCard(
        borderColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Column {
            HomeScreenCardHeading(text = "Sleep")
            JetLaggedHeaderTabs(
                onTabSelected = { selectedTab = it },
                selectedTab = selectedTab,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            JetLaggedTimeGraph(
                sleepState
            )
        }
    }
}

@Composable
private fun JetLaggedTimeGraph(
    sleepGraphData: SleepGraphData,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val hours = (sleepGraphData.earliestStartHour..23) + (0..sleepGraphData.latestEndHour)

    TimeGraph(
        modifier = modifier
            .horizontalScroll(scrollState)
            .wrapContentSize(),
        dayItemsCount = sleepGraphData.sleepDayData.size,
        hoursHeader = {
            HoursHeader(hours)
        },
        dayLabel = { index ->
            val data = sleepGraphData.sleepDayData[index]
            DayLabel(data.startDate.dayOfWeek)
        },
        bar = { index ->
            val data = sleepGraphData.sleepDayData[index]
            // We have access to Modifier.timeGraphBar() as we are now in TimeGraphScope
            SleepBar(
                sleepData = data,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .timeGraphBar(
                        start = data.firstSleepStart,
                        end = data.lastSleepEnd,
                        hours = hours,
                    )
            )
        }
    )
}

@Composable
private fun DayLabel(dayOfWeek: DayOfWeek) {
    Text(
        dayOfWeek.getDisplayName(
            TextStyle.SHORT, Locale.getDefault()
        ),
        Modifier
            .height(24.dp)
            .padding(start = 8.dp, end = 24.dp),
        style = SmallHeadingStyle,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HoursHeader(hours: List<Int>) {
    val brushColors = listOf(
        JetLaggedTheme.extraColors.sleepChartPrimary,
        JetLaggedTheme.extraColors.sleepChartSecondary,
    )
    Row(
        Modifier
            .padding(bottom = 16.dp)
            .drawBehind {
                val brush = Brush.linearGradient(brushColors)
                drawRoundRect(
                    brush,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            }
    ) {
        hours.forEach {
            Text(
                text = "$it",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(50.dp)
                    .padding(vertical = 4.dp),
                style = SmallHeadingStyle
            )
        }
    }
}
