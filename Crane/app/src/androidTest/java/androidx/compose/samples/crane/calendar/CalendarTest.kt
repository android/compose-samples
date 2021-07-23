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
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.FirstDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.FirstLastDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.LastDay
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.NoSelected
import androidx.compose.samples.crane.calendar.model.DaySelectedStatus.Selected
import androidx.compose.samples.crane.data.DatesRepository
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollToKeyAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.printToLog
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CalendarTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<CalendarActivity>()

    @Inject
    lateinit var datesRepository: DatesRepository

    @Before
    fun setUp() {
        hiltRule.inject()

        composeTestRule.setContent {
            CraneTheme {
                Surface {
                    CalendarScreen(onBackPressed = {})
                }
            }
        }
    }

    @ExperimentalTestApi
    @Test
    fun scrollsToTheBottom() {
        composeTestRule.onNodeWithText("January 1 2020").assertIsDisplayed()
        composeTestRule.onNode(hasScrollToKeyAction()).performScrollToKey("2020/12/4")
        composeTestRule.onNodeWithText("December 31 2020").performClick()
        assert(datesRepository.datesSelected.toString() == "Dec 31")
    }

    @Test
    fun onDaySelected() {
        composeTestRule.onNodeWithText("January 1 2020").assertIsDisplayed()
        composeTestRule.onNodeWithText("January 2 2020")
            .assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("January 3 2020").assertIsDisplayed()

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertTextEquals("January 1 2020")
        datesNoSelected[1].assertTextEquals("January 3 2020")

        composeTestRule.onDateNode(FirstLastDay).assertTextEquals("January 2 2020")
    }

    @Test
    fun twoDaysSelected() {
        composeTestRule.onNodeWithText("January 2 2020")
            .assertIsDisplayed().performClick()

        val datesNoSelectedOneClick = composeTestRule.onDateNodes(NoSelected)
        composeTestRule.onRoot().printToLog("JOLO")
        datesNoSelectedOneClick[0].assertTextEquals("January 1 2020")
        datesNoSelectedOneClick[1].assertTextEquals("January 3 2020")

        composeTestRule.onNodeWithText("January 4 2020")
            .assertIsDisplayed().performClick()

        composeTestRule.onDateNode(FirstDay).assertTextEquals("January 2 2020")
        composeTestRule.onDateNode(Selected).assertTextEquals("January 3 2020")
        composeTestRule.onDateNode(LastDay).assertTextEquals("January 4 2020")

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertTextEquals("January 1 2020")
        datesNoSelected[1].assertTextEquals("January 5 2020")
    }
}

private fun ComposeTestRule.onDateNode(status: DaySelectedStatus) = onNode(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)

private fun ComposeTestRule.onDateNodes(status: DaySelectedStatus) = onAllNodes(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)
