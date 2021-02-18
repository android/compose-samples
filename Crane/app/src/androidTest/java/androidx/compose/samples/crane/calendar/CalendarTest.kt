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
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Ignore
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

    @Ignore("performScrollTo doesn't work with LazyLists: issuetracker.google.com/178483889")
    @Test
    fun scrollsToTheBottom() {
        composeTestRule.onNodeWithContentDescription("January 1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("December 31").performScrollTo().performClick()
        assert(datesRepository.datesSelected.toString() == "Dec 31")
    }

    @Test
    fun onDaySelected() {
        composeTestRule.onNodeWithContentDescription("January 1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("January 2")
            .assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription("January 3").assertIsDisplayed()

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertContentDescriptionEquals("January 1")
        datesNoSelected[1].assertContentDescriptionEquals("January 3")

        composeTestRule.onDateNode(FirstLastDay).assertContentDescriptionEquals("January 2")
    }

    @Test
    fun twoDaysSelected() {
        composeTestRule.onNodeWithContentDescription("January 2")
            .assertIsDisplayed().performClick()

        val datesNoSelectedOneClick = composeTestRule.onDateNodes(NoSelected)
        datesNoSelectedOneClick[0].assertContentDescriptionEquals("January 1")
        datesNoSelectedOneClick[1].assertContentDescriptionEquals("January 3")

        composeTestRule.onNodeWithContentDescription("January 4")
            .assertIsDisplayed().performClick()

        composeTestRule.onDateNode(FirstDay).assertContentDescriptionEquals("January 2")
        composeTestRule.onDateNode(Selected).assertContentDescriptionEquals("January 3")
        composeTestRule.onDateNode(LastDay).assertContentDescriptionEquals("January 4")

        val datesNoSelected = composeTestRule.onDateNodes(NoSelected)
        datesNoSelected[0].assertContentDescriptionEquals("January 1")
        datesNoSelected[1].assertContentDescriptionEquals("January 5")
    }
}

private fun ComposeTestRule.onDateNode(status: DaySelectedStatus) = onNode(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)

private fun ComposeTestRule.onDateNodes(status: DaySelectedStatus) = onAllNodes(
    SemanticsMatcher.expectValue(DayStatusKey, status)
)
