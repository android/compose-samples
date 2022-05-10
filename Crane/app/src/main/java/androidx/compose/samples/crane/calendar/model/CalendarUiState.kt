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

package androidx.compose.samples.crane.calendar.model

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class CalendarUiState(
    val selectedStartDate: LocalDate? = null,
    val selectedEndDate: LocalDate? = null,
    val animateDirection: AnimationDirection? = null
) {

    val numberSelectedDays: Float
        get() {
            if (selectedStartDate == null) return 0f
            if (selectedEndDate == null) return 1f
            return ChronoUnit.DAYS.between(selectedStartDate, selectedEndDate.plusDays(1)).toFloat()
        }

    val hasSelectedDates: Boolean
        get() {
            return selectedStartDate != null || selectedEndDate != null
        }

    val selectedDatesFormatted: String
        get() {
            if (selectedStartDate == null) return ""
            var output = selectedStartDate.format(SHORT_DATE_FORMAT)
            if (selectedEndDate != null) {
                output += " - ${selectedEndDate.format(SHORT_DATE_FORMAT)}"
            }
            return output
        }

    fun hasSelectedPeriodOverlap(start: LocalDate, end: LocalDate): Boolean {
        if (!hasSelectedDates) return false
        if (selectedStartDate == null && selectedEndDate == null) return false
        if (selectedStartDate == start || selectedStartDate == end) return true
        if (selectedEndDate == null) {
            return !selectedStartDate!!.isBefore(start) && !selectedStartDate.isAfter(end)
        }
        return !end.isBefore(selectedStartDate) && !start.isAfter(selectedEndDate)
    }

    fun isDateInSelectedPeriod(date: LocalDate): Boolean {
        if (selectedStartDate == null) return false
        if (selectedStartDate == date) return true
        if (selectedEndDate == null) return false
        if (date.isBefore(selectedStartDate) ||
            date.isAfter(selectedEndDate)
        ) return false
        return true
    }

    fun getNumberSelectedDaysInWeek(currentWeekStartDate: LocalDate, month: YearMonth): Int {
        var countSelected = 0
        var currentDate = currentWeekStartDate
        for (i in 0 until CalendarState.DAYS_IN_WEEK) {
            if (isDateInSelectedPeriod(currentDate) && currentDate.month == month.month) {
                countSelected++
            }
            currentDate = currentDate.plusDays(1)
        }
        return countSelected
    }

    /**
     * Returns the number of selected days from the start or end of the week, depending on direction.
     */
    fun selectedStartOffset(currentWeekStartDate: LocalDate, yearMonth: YearMonth): Int {
        return if (animateDirection == null || animateDirection.isForwards()) {
            var startDate = currentWeekStartDate
            var startOffset = 0
            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.plusDays(1)
            }
            startOffset
        } else {
            var startDate = currentWeekStartDate.plusDays(6)
            var startOffset = 0

            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.minusDays(1)
            }
            7 - startOffset
        }
    }

    fun isLeftHighlighted(beginningWeek: LocalDate?, month: YearMonth): Boolean {
        return if (beginningWeek != null) {
            if (month.month.value != beginningWeek.month.value) {
                false
            } else {
                val beginningWeekSelected = isDateInSelectedPeriod(beginningWeek)
                val lastDayPreviousWeek = beginningWeek.minusDays(1)
                isDateInSelectedPeriod(lastDayPreviousWeek) && beginningWeekSelected
            }
        } else {
            false
        }
    }

    fun isRightHighlighted(
        beginningWeek: LocalDate?,
        month: YearMonth
    ): Boolean {
        val lastDayOfTheWeek = beginningWeek?.plusDays(6)
        return if (lastDayOfTheWeek != null) {
            if (month.month.value != lastDayOfTheWeek.month.value) {
                false
            } else {
                val lastDayOfTheWeekSelected = isDateInSelectedPeriod(lastDayOfTheWeek)
                val firstDayNextWeek = lastDayOfTheWeek.plusDays(1)
                isDateInSelectedPeriod(firstDayNextWeek) && lastDayOfTheWeekSelected
            }
        } else {
            false
        }
    }

    fun dayDelay(currentWeekStartDate: LocalDate): Int {
        if (selectedStartDate == null && selectedEndDate == null) return 0
        // if selected week contains start date, don't have any delay
        val endWeek = currentWeekStartDate.plusDays(6)
        return if (animateDirection != null && animateDirection.isBackwards()) {
            if (selectedEndDate?.isBefore(currentWeekStartDate) == true ||
                selectedEndDate?.isAfter(endWeek) == true
            ) {
                // selected end date is not in current week - return actual days calc difference
                abs(ChronoUnit.DAYS.between(endWeek, selectedEndDate)).toInt()
            } else {
                0
            }
        } else {
            if (selectedStartDate?.isBefore(currentWeekStartDate) == true ||
                selectedStartDate?.isAfter(endWeek) == true
            ) {
                // selected start date is not in current week
                abs(ChronoUnit.DAYS.between(currentWeekStartDate, selectedStartDate)).toInt()
            } else {
                0
            }
        }
    }

    fun monthOverlapSelectionDelay(
        currentWeekStartDate: LocalDate,
        week: Week
    ): Int {
        return if (animateDirection?.isBackwards() == true) {
            val endWeek = currentWeekStartDate.plusDays(6)
            val isStartInADifferentMonth = endWeek.month != week.yearMonth.month
            if (isStartInADifferentMonth) {
                var currentDate = endWeek
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.minusDays(1)
                }
                offset
            } else {
                0
            }
        } else {
            val isStartInADifferentMonth = currentWeekStartDate.month != week.yearMonth.month
            return if (isStartInADifferentMonth) {
                var currentDate = currentWeekStartDate
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.plusDays(1)
                }
                offset
            } else {
                0
            }
        }
    }

    fun setDates(newFrom: LocalDate?, newTo: LocalDate?): CalendarUiState {
        return if (newTo == null) {
            copy(selectedStartDate = newFrom)
        } else {
            copy(selectedStartDate = newFrom, selectedEndDate = newTo)
        }
    }

    companion object {
        private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd")
    }
}
