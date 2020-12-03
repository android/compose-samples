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

package androidx.compose.samples.crane.calendar

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.calendar.model.CalendarDay
import androidx.compose.samples.crane.calendar.model.CalendarMonth
import androidx.compose.samples.crane.calendar.model.DayOfWeek
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus
import androidx.compose.samples.crane.data.CalendarYear
import androidx.compose.samples.crane.data.DatesLocalDataSource
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.util.Circle
import androidx.compose.samples.crane.util.SemiRect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.accessibilityLabel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

typealias CalendarWeek = List<CalendarDay>

@Composable
fun Calendar(
    calendarYear: CalendarYear,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableColumn(modifier = modifier) {
        Spacer(Modifier.preferredHeight(32.dp))
        for (month in calendarYear) {
            Month(month = month, onDayClicked = onDayClicked)
            Spacer(Modifier.preferredHeight(32.dp))
        }
    }
}

@Composable
private fun Month(
    modifier: Modifier = Modifier,
    month: CalendarMonth,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit
) {
    Column(modifier = modifier) {
        MonthHeader(
            modifier = Modifier.padding(horizontal = 30.dp),
            month = month.name,
            year = month.year
        )

        // Expanding width and centering horizontally
        val contentModifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
        DaysOfWeek(modifier = contentModifier)
        for (week in month.weeks.value) {
            Week(
                modifier = contentModifier,
                week = week,
                month = month,
                onDayClicked = { day ->
                    onDayClicked(day, month)
                }
            )
            Spacer(Modifier.preferredHeight(8.dp))
        }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.weight(1f),
            text = month,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = year,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
private fun Week(
    modifier: Modifier = Modifier,
    month: CalendarMonth,
    week: CalendarWeek,
    onDayClicked: (CalendarDay) -> Unit
) {
    val (leftFillColor, rightFillColor) = getLeftRightWeekColors(week, month)

    Row(modifier = modifier) {
        val spaceModifiers = Modifier.weight(1f).preferredHeightIn(max = CELL_SIZE)
        Surface(modifier = spaceModifiers, color = leftFillColor) {
            Spacer(Modifier.fillMaxHeight())
        }
        for (day in week) {
            Day(
                day,
                onDayClicked,
                Modifier.semantics {
                    accessibilityLabel = "${month.name} ${day.value}"
                    dayStatusProperty = day.status
                }
            )
        }
        Surface(modifier = spaceModifiers, color = rightFillColor) {
            Spacer(Modifier.fillMaxHeight())
        }
    }
}

@Composable
private fun DaysOfWeek(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (day in DayOfWeek.values()) {
            Day(name = day.name.take(1))
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    onDayClicked: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = day.status != DaySelectedStatus.NonClickable
    DayContainer(
        modifier = modifier.clickable(enabled) {
            if (day.status != DaySelectedStatus.NonClickable) onDayClicked(day)
        },
        backgroundColor = day.status.color(MaterialTheme.colors)
    ) {
        DayStatusContainer(status = day.status) {
            Text(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                text = day.value,
                style = MaterialTheme.typography.body1.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun Day(name: String) {
    DayContainer {
        Text(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            text = name,
            style = MaterialTheme.typography.caption.copy(Color.White.copy(alpha = 0.6f))
        )
    }
}

@Composable
private fun DayContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    children: @Composable () -> Unit
) {
    // What if this doesn't fit the screen? - LayoutFlexible(1f) + LayoutAspectRatio(1f)
    Surface(
        modifier = modifier.preferredSize(width = CELL_SIZE, height = CELL_SIZE),
        color = backgroundColor
    ) {
        children()
    }
}

@Composable
private fun DayStatusContainer(
    status: DaySelectedStatus,
    children: @Composable () -> Unit
) {
    if (status.isMarked()) {
        Box {
            val color = MaterialTheme.colors.secondary
            Circle(color = color)
            if (status == DaySelectedStatus.FirstDay) {
                SemiRect(color = color, lookingLeft = false)
            } else if (status == DaySelectedStatus.LastDay) {
                SemiRect(color = color, lookingLeft = true)
            }
            children()
        }
    } else {
        children()
    }
}

private fun DaySelectedStatus.color(theme: Colors): Color = when (this) {
    DaySelectedStatus.Selected -> theme.secondary
    else -> Color.Transparent
}

@Composable
private fun getLeftRightWeekColors(week: CalendarWeek, month: CalendarMonth): Pair<Color, Color> {
    val materialColors = MaterialTheme.colors

    val firstDayOfTheWeek = week[0].value
    val leftFillColor = if (firstDayOfTheWeek.isNotEmpty()) {
        val lastDayPreviousWeek = month.getPreviousDay(firstDayOfTheWeek.toInt())
        if (lastDayPreviousWeek?.status?.isMarked() == true && week[0].status.isMarked()) {
            materialColors.secondary
        } else {
            Color.Transparent
        }
    } else {
        Color.Transparent
    }

    val lastDayOfTheWeek = week[6].value
    val rightFillColor = if (lastDayOfTheWeek.isNotEmpty()) {
        val firstDayNextWeek = month.getNextDay(lastDayOfTheWeek.toInt())
        if (firstDayNextWeek?.status?.isMarked() == true && week[6].status.isMarked()) {
            materialColors.secondary
        } else {
            Color.Transparent
        }
    } else {
        Color.Transparent
    }

    return leftFillColor to rightFillColor
}

private fun DaySelectedStatus.isMarked(): Boolean {
    return when (this) {
        DaySelectedStatus.Selected -> true
        DaySelectedStatus.FirstDay -> true
        DaySelectedStatus.LastDay -> true
        DaySelectedStatus.FirstLastDay -> true
        else -> false
    }
}

private val CELL_SIZE = 48.dp

val DayStatusKey = SemanticsPropertyKey<DaySelectedStatus>("DayStatusKey")
var SemanticsPropertyReceiver.dayStatusProperty by DayStatusKey

@Preview
@Composable
fun DayPreview() {
    CraneTheme {
        Calendar(DatesLocalDataSource().year2020, onDayClicked = { _, _ -> })
    }
}
