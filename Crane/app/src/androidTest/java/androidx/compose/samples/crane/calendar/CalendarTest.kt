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

import androidx.compose.material.Surface
import androidx.compose.samples.crane.base.ServiceLocator
import androidx.compose.samples.crane.calendar.model.CalendarDay
import androidx.compose.samples.crane.calendar.model.CalendarMonth
import androidx.compose.samples.crane.calendar.model.DaySelected
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.FirstDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.FirstLastDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.LastDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.NoSelected
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.Selected
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.ui.test.ComposeTestRule
import androidx.ui.test.SemanticsMatcher
import androidx.ui.test.assertLabelEquals
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.performClick
import androidx.ui.test.performScrollTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    var dateSelected = ""
    private val onDayClicked: (CalendarDay, CalendarMonth) -> Unit = { day, month ->
        dateSelected = "${month.name} ${day.value}"
        ServiceLocator.datesSelected.daySelected(
            DaySelected(
                day = day.value.toInt(),
                month = month
            )
        )
    }

    @Before
    fun setUp() {
        composeTestRule.setContent {
            CraneTheme {
                Surface {
                    Calendar(onDayClicked)
                }
            }
        }
    }

    @After
    fun tearDown() {
        ServiceLocator.datesSelected.clearDates()
    }

    @Test
    fun scrollsToTheBottom() {
        composeTestRule.onNodeWithLabel("January 1").assertExists()
        composeTestRule.onNodeWithLabel("December 31").performScrollTo().performClick()
        assert(dateSelected == "December 31")
    }

    @Test
    fun onDaySelected() {
        composeTestRule.onNodeWithLabel("January 1").assertExists()
        composeTestRule.onNodeWithLabel("January 2").assertExists().performClick()
        composeTestRule.onNodeWithLabel("January 3").assertExists()

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertLabelEquals("January 1")
        datesNoSelected[1].assertLabelEquals("January 3")

        composeTestRule.onDateNode(FirstLastDay).assertLabelEquals("January 2")
    }

    @Test
    fun twoDaysSelected() {
        composeTestRule.onNodeWithLabel("January 2").assertExists().performClick()

        val datesNoSelectedOneClick = composeTestRule.onDateNodes(NoSelected)
        datesNoSelectedOneClick[0].assertLabelEquals("January 1")
        datesNoSelectedOneClick[1].assertLabelEquals("January 3")

        composeTestRule.onNodeWithLabel("January 4").assertExists().performClick()

        composeTestRule.onDateNode(FirstDay).assertLabelEquals("January 2")
        composeTestRule.onDateNode(Selected).assertLabelEquals("January 3")
        composeTestRule.onDateNode(LastDay).assertLabelEquals("January 4")

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertLabelEquals("January 1")
        datesNoSelected[1].assertLabelEquals("January 5")
    }
}

private fun ComposeTestRule.onDateNode(status: DaySelectedStatus) = onNode(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)

private fun ComposeTestRule.onDateNodes(status: DaySelectedStatus) = onAllNodes(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)
