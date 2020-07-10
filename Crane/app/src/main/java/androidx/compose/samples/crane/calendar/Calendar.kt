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

package androidx.compose.samples.crane.calendar

import androidx.compose.Composable
import androidx.compose.samples.crane.calendar.data.year2020
import androidx.compose.samples.crane.calendar.model.CalendarDay
import androidx.compose.samples.crane.calendar.model.CalendarMonth
import androidx.compose.samples.crane.calendar.model.DayOfWeek
import androidx.compose.samples.crane.calendar.model.SelectedStatus
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.util.Circle
import androidx.compose.samples.crane.util.SemiRect
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredSize
import androidx.ui.layout.wrapContentSize
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

typealias CalendarWeek = List<CalendarDay>

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit
) {
    ScrollableColumn(modifier = modifier) {
        Spacer(Modifier.preferredHeight(32.dp))
        for (month in year2020) {
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
            modifier = Modifier.padding(start = 30.dp, end = 30.dp),
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
            modifier = Modifier.gravity(Alignment.CenterVertically),
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
        val spaceModifiers = Modifier.weight(1f).preferredHeightIn(maxHeight = CELL_SIZE)
        Surface(modifier = spaceModifiers, color = leftFillColor) {
            Spacer(Modifier.fillMaxHeight())
        }
        for (day in week) {
            Day(day = day, onDayClicked = onDayClicked)
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
private fun Day(day: CalendarDay, onDayClicked: (CalendarDay) -> Unit) {
    val enabled = day.status != SelectedStatus.NON_CLICKABLE
    DayContainer(
        modifier = Modifier.clickable(enabled) {
            if (day.status != SelectedStatus.NON_CLICKABLE) onDayClicked(day)
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
    status: SelectedStatus,
    children: @Composable () -> Unit
) {
    if (status.isMarked()) {
        Stack {
            val color = MaterialTheme.colors.secondary

            WithConstraints {
                Circle(constraints = constraints, color = color)
                if (status == SelectedStatus.FIRST_DAY) {
                    SemiRect(constraints = constraints, color = color, lookingLeft = false)
                } else if (status == SelectedStatus.LAST_DAY) {
                    SemiRect(constraints = constraints, color = color, lookingLeft = true)
                }
                children()
            }
        }
    } else {
        children()
    }
}

private fun SelectedStatus.color(theme: ColorPalette): Color = when (this) {
    SelectedStatus.SELECTED -> theme.secondary
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

private fun SelectedStatus.isMarked(): Boolean {
    return when (this) {
        SelectedStatus.SELECTED -> true
        SelectedStatus.FIRST_DAY -> true
        SelectedStatus.LAST_DAY -> true
        SelectedStatus.FIRST_LAST_DAY -> true
        else -> false
    }
}

private val CELL_SIZE = 48.dp

@Preview
@Composable
fun DayPreview() {
    CraneTheme {
        Calendar(onDayClicked = { _, _ -> })
    }
}
