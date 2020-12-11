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

data class CalendarMonth(
    val name: String,
    val year: String,
    val numDays: Int,
    val monthNumber: Int,
    val startDayOfWeek: DayOfWeek
) {
    private val days = mutableListOf<CalendarDay>().apply {
        // Add offset of the start of the month
        for (i in 1..startDayOfWeek.ordinal) {
            add(
                CalendarDay(
                    "",
                    DaySelectedStatus.NonClickable
                )
            )
        }
        // Add days of the month
        for (i in 1..numDays) {
            add(
                CalendarDay(
                    i.toString(),
                    DaySelectedStatus.NoSelected
                )
            )
        }
    }.toList()

    fun getDay(day: Int): CalendarDay {
        return days[day + startDayOfWeek.ordinal - 1]
    }

    fun getPreviousDay(day: Int): CalendarDay? {
        if (day <= 1) return null
        return getDay(day - 1)
    }

    fun getNextDay(day: Int): CalendarDay? {
        if (day >= numDays) return null
        return getDay(day + 1)
    }

    val weeks = lazy { days.chunked(7).map { completeWeek(it) } }

    private fun completeWeek(list: List<CalendarDay>): List<CalendarDay> {
        var gapsToFill = 7 - list.size

        return if (gapsToFill != 0) {
            val mutableList = list.toMutableList()
            while (gapsToFill > 0) {
                mutableList.add(
                    CalendarDay(
                        "",
                        DaySelectedStatus.NonClickable
                    )
                )
                gapsToFill--
            }
            mutableList
        } else {
            list
        }
    }
}
