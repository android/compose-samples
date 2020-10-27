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

package androidx.compose.samples.crane.calendar.model

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.data.CalendarYear

class DatesSelectedState(private val year: CalendarYear) {
    private var from by mutableStateOf(DaySelectedEmpty)
    private var to by mutableStateOf(DaySelectedEmpty)

    override fun toString(): String {
        if (from == DaySelectedEmpty && to == DaySelectedEmpty) return ""
        var output = from.toString()
        if (to != DaySelectedEmpty) {
            output += " - $to"
        }
        return output
    }

    fun daySelected(newDate: DaySelected) {
        if (from == DaySelectedEmpty && to == DaySelectedEmpty) {
            setDates(newDate, DaySelectedEmpty)
        } else if (from != DaySelectedEmpty && to != DaySelectedEmpty) {
            clearDates()
            daySelected(newDate = newDate)
        } else if (from == DaySelectedEmpty) {
            if (newDate < to) setDates(newDate, to)
            else if (newDate > to) setDates(to, newDate)
        } else if (to == DaySelectedEmpty) {
            if (newDate < from) setDates(newDate, from)
            else if (newDate > from) setDates(from, newDate)
        }
    }

    private fun setDates(newFrom: DaySelected, newTo: DaySelected) {
        if (newTo == DaySelectedEmpty) {
            from = newFrom
            from.calendarDay.value.status = DaySelectedStatus.FirstLastDay
        } else {
            from = newFrom.apply { calendarDay.value.status = DaySelectedStatus.FirstDay }
            selectDatesInBetween(newFrom, newTo)
            to = newTo.apply { calendarDay.value.status = DaySelectedStatus.LastDay }
        }
    }

    private fun selectDatesInBetween(from: DaySelected, to: DaySelected) {
        if (from.month == to.month) {
            for (i in (from.day + 1) until to.day)
                from.month.getDay(i).status = DaySelectedStatus.Selected
        } else {
            // Fill from's month
            for (i in (from.day + 1) until from.month.numDays) {
                from.month.getDay(i).status = DaySelectedStatus.Selected
            }
            from.month.getDay(from.month.numDays).status = DaySelectedStatus.LastDay
            // Fill in-between months
            for (i in (from.month.monthNumber + 1) until to.month.monthNumber) {
                val month = year[i - 1]
                month.getDay(1).status = DaySelectedStatus.FirstDay
                for (j in 2 until month.numDays) {
                    month.getDay(j).status = DaySelectedStatus.Selected
                }
                month.getDay(month.numDays).status = DaySelectedStatus.LastDay
            }
            // Fill to's month
            to.month.getDay(1).status = DaySelectedStatus.FirstDay
            for (i in 2 until to.day) {
                to.month.getDay(i).status = DaySelectedStatus.Selected
            }
        }
    }

    @VisibleForTesting
    fun clearDates() {
        if (from != DaySelectedEmpty || to != DaySelectedEmpty) {
            // Unselect dates from the same month
            if (from.month == to.month) {
                for (i in from.day..to.day)
                    from.month.getDay(i).status = DaySelectedStatus.NoSelected
            } else {
                // Unselect from's month
                for (i in from.day..from.month.numDays) {
                    from.month.getDay(i).status = DaySelectedStatus.NoSelected
                }
                // Fill in-between months
                for (i in (from.month.monthNumber + 1) until to.month.monthNumber) {
                    val month = year[i - 1]
                    for (j in 1..month.numDays) {
                        month.getDay(j).status = DaySelectedStatus.NoSelected
                    }
                }
                // Fill to's month
                for (i in 1..to.day) {
                    to.month.getDay(i).status = DaySelectedStatus.NoSelected
                }
            }
        }
        from.calendarDay.value.status = DaySelectedStatus.NoSelected
        from = DaySelectedEmpty
        to.calendarDay.value.status = DaySelectedStatus.NoSelected
        to = DaySelectedEmpty
    }
}
