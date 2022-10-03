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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarUiStateTest {

    @Test
    fun dateOverlapsWithWeekPeriod() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 4, 14),
            selectedEndDate = LocalDate.of(2022, 4, 16)
        )
        val result = calendarState.hasSelectedPeriodOverlap(
            LocalDate.of(2022, 4, 11),
            LocalDate.of(2022, 4, 17)
        )

        assertTrue(result)
    }

    @Test
    fun dateOverlapsWithNoSelectedEndDate() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 4, 14),
            selectedEndDate = null
        )
        val result = calendarState.hasSelectedPeriodOverlap(
            LocalDate.of(2022, 4, 11),
            LocalDate.of(2022, 4, 17)
        )

        assertTrue(result)
    }

    @Test
    fun datesDoNotOverlap() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 4, 14),
            selectedEndDate = LocalDate.of(2022, 4, 16)
        )
        val result = calendarState.hasSelectedPeriodOverlap(
            LocalDate.of(2022, 5, 11),
            LocalDate.of(2022, 5, 17)
        )
        assertFalse(result)
    }

    @Test
    fun testIsLeftWeekHighlighted() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 2, 5),
            selectedEndDate = LocalDate.of(2022, 2, 10)
        )
        val resultBeginningWeek = calendarState.isLeftHighlighted(
            LocalDate.of(2022, 2, 1),
            YearMonth.of(2022, 2)
        )
        assertFalse(resultBeginningWeek)
        val resultWeekLater = calendarState.isLeftHighlighted(
            LocalDate.of(2022, 2, 7),
            YearMonth.of(2022, 2)
        )
        assertTrue(resultWeekLater)
    }

    @Test
    fun testIsRightWeekHighlighted() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 2, 5),
            selectedEndDate = LocalDate.of(2022, 2, 10)
        )
        val resultBeginningWeek = calendarState.isRightHighlighted(
            LocalDate.of(2022, 2, 1),
            YearMonth.of(2022, 2)
        )
        assertTrue(resultBeginningWeek)
        val resultWeekLater = calendarState.isRightHighlighted(
            LocalDate.of(2022, 2, 7),
            YearMonth.of(2022, 2)
        )
        assertFalse(resultWeekLater)
    }

    @Test
    fun testStartAnimationOffsetCalculation_Forwards_WithinSameMonth() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 2, 15),
            selectedEndDate = LocalDate.of(2022, 2, 22)
        )

        val offset = calendarState.selectedStartOffset(
            LocalDate.of(2022, 2, 14),
            YearMonth.of(2022, 2)
        )
        assertEquals(1, offset)
        val offsetSecondWeek = calendarState.selectedStartOffset(
            LocalDate.of(2022, 2, 21),
            YearMonth.of(2022, 2)
        )
        assertEquals(0, offsetSecondWeek)
    }

    @Test
    fun testStartAnimationOffsetCalculation_Backwards_WithinSameMonth() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 2, 15),
            selectedEndDate = LocalDate.of(2022, 2, 22),
            animateDirection = AnimationDirection.BACKWARDS
        )

        val offset = calendarState.selectedStartOffset(
            LocalDate.of(2022, 2, 14),
            YearMonth.of(2022, 2)
        )
        assertEquals(7, offset)
        val offsetSecondWeek = calendarState.selectedStartOffset(
            LocalDate.of(2022, 2, 21),
            YearMonth.of(2022, 2)
        )

        assertEquals(2, offsetSecondWeek)
    }

    @Test
    fun testStartAnimationOffsetCalculation_Forwards_OverTwoMonths() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 3, 23),
            selectedEndDate = LocalDate.of(2022, 4, 3),
            animateDirection = AnimationDirection.FORWARDS
        )

        val offset = calendarState.selectedStartOffset(
            LocalDate.of(2022, 3, 21),
            YearMonth.of(2022, 3)
        )
        assertEquals(2, offset)
        val offsetSecondWeek = calendarState.selectedStartOffset(
            LocalDate.of(2022, 3, 28),
            YearMonth.of(2022, 3)
        )

        assertEquals(0, offsetSecondWeek)
        val offsetFirstWeekSecondMonth = calendarState.selectedStartOffset(
            LocalDate.of(2022, 3, 28),
            YearMonth.of(2022, 4)
        )
        assertEquals(4, offsetFirstWeekSecondMonth)
    }

    @Test
    fun testStartAnimationOffsetCalculation_Backwards_OverTwoMonths() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 3, 23),
            selectedEndDate = LocalDate.of(2022, 3, 31),
            animateDirection = AnimationDirection.BACKWARDS
        )

        val offsetSecondWeek = calendarState.selectedStartOffset(
            LocalDate.of(2022, 3, 21),
            YearMonth.of(2022, 3)
        )

        assertEquals(7, offsetSecondWeek)
        val offsetFirstWeekSecondMonth = calendarState.selectedStartOffset(
            LocalDate.of(2022, 3, 28),
            YearMonth.of(2022, 3)
        )
        assertEquals(4, offsetFirstWeekSecondMonth)
    }
    @Test
    fun testStartAnimationOffsetCalculation_Backwards_OverTwoMonths_June() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 5, 27),
            selectedEndDate = LocalDate.of(2022, 6, 4),
            animateDirection = AnimationDirection.BACKWARDS
        )

        val offsetSecondWeek = calendarState.selectedStartOffset(
            LocalDate.of(2022, 5, 23),
            YearMonth.of(2022, 5)
        )

        assertEquals(7, offsetSecondWeek) // should be all the way to the end
        val offsetFirstWeekSecondMonth = calendarState.selectedStartOffset(
            LocalDate.of(2022, 5, 30),
            YearMonth.of(2022, 5)
        )
        assertEquals(2, offsetFirstWeekSecondMonth)

        val offsetThird = calendarState.selectedStartOffset(
            LocalDate.of(2022, 5, 30),
            YearMonth.of(2022, 6)
        )
        assertEquals(6, offsetThird)
    }

    @Test
    fun totalNumberDaySelected() {
        val calendarState = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 5, 27),
            selectedEndDate = LocalDate.of(2022, 6, 10),
            animateDirection = AnimationDirection.BACKWARDS
        )

        assertEquals(15f, calendarState.numberSelectedDays)

        val calendarStateNoEnd = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 5, 27),
            animateDirection = AnimationDirection.BACKWARDS
        )

        assertEquals(1f, calendarStateNoEnd.numberSelectedDays)

        val calendarStateNoStart = CalendarUiState(
            animateDirection = AnimationDirection.BACKWARDS
        )

        assertEquals(0f, calendarStateNoStart.numberSelectedDays)

        val calendarStateShort = CalendarUiState(
            selectedStartDate = LocalDate.of(2022, 5, 27),
            selectedEndDate = LocalDate.of(2022, 5, 28),
            animateDirection = AnimationDirection.BACKWARDS
        )

        assertEquals(2f, calendarStateShort.numberSelectedDays)
    }
}
