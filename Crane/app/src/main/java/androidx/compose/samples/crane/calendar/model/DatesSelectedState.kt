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

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.samples.crane.calendar.data.year2020
import androidx.compose.setValue

class DatesSelectedState {
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
            from = DaySelectedEmpty
            to = DaySelectedEmpty
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
            from.calendarDay.value.status = SelectedStatus.FIRST_LAST_DAY
        } else {
            from = newFrom.apply { calendarDay.value.status = SelectedStatus.FIRST_DAY }
            selectDatesInBetween(newFrom, newTo)
            to = newTo.apply { calendarDay.value.status = SelectedStatus.LAST_DAY }
        }
    }

    private fun selectDatesInBetween(from: DaySelected, to: DaySelected) {
        if (from.month == to.month) {
            for (i in (from.day + 1) until to.day)
                from.month.getDay(i).status = SelectedStatus.SELECTED
        } else {
            // Fill from's month
            for (i in (from.day + 1) until from.month.numDays) {
                from.month.getDay(i).status = SelectedStatus.SELECTED
            }
            from.month.getDay(from.month.numDays).status = SelectedStatus.LAST_DAY
            // Fill in-between months
            for (i in (from.month.monthNumber + 1) until to.month.monthNumber) {
                val month = year2020[i - 1]
                month.getDay(1).status = SelectedStatus.FIRST_DAY
                for (j in 2 until month.numDays) {
                    month.getDay(j).status = SelectedStatus.SELECTED
                }
                month.getDay(month.numDays).status = SelectedStatus.LAST_DAY
            }
            // Fill to's month
            to.month.getDay(1).status = SelectedStatus.FIRST_DAY
            for (i in 2 until to.day) {
                to.month.getDay(i).status = SelectedStatus.SELECTED
            }
        }
    }

    private fun clearDates() {
        if (from != DaySelectedEmpty && to != DaySelectedEmpty) {
            // Unselect dates from the same month
            if (from.month == to.month) {
                for (i in from.day..to.day)
                    from.month.getDay(i).status = SelectedStatus.NO_SELECTED
            } else {
                // Unselect from's month
                for (i in from.day..from.month.numDays) {
                    from.month.getDay(i).status = SelectedStatus.NO_SELECTED
                }
                // Fill in-between months
                for (i in (from.month.monthNumber + 1) until to.month.monthNumber) {
                    val month = year2020[i - 1]
                    for (j in 1..month.numDays) {
                        month.getDay(j).status = SelectedStatus.NO_SELECTED
                    }
                }
                // Fill to's month
                for (i in 1..to.day) {
                    to.month.getDay(i).status = SelectedStatus.NO_SELECTED
                }
            }
        }
    }
}
