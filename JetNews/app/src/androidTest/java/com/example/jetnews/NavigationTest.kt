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

package com.example.jetnews

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var activity: ComponentActivity

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity {
            activity = it
        }
        composeTestRule.launchJetNewsApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun open_find_AppDrawer() {
        with(composeTestRule) {
            onNodeWithTag("appDrawer").performClick()
            onNodeWithTag("appDrawerButtonHome").assertIsDisplayed()
            onNodeWithTag("appDrawerButtonInterests").assertIsDisplayed()
        }
    }

    @Test
    fun select_drawerButtons_and_drawer_closed() {
        with(composeTestRule) {
            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonHome").performClick()
            onNodeWithTag("appDrawerButtonHome").assertIsNotDisplayed()
            onNodeWithTag("appDrawerButtonInterests").assertIsNotDisplayed()

            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonInterests").performClick()
            onNodeWithTag("appDrawerButtonHome").assertIsNotDisplayed()
            onNodeWithTag("appDrawerButtonInterests").assertIsNotDisplayed()
        }
    }

    @Test
    fun select_drawerButtons_and_navigate_to_screen() {
        with(composeTestRule) {
            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonInterests").performClick()
            onNodeWithTag("topAppBarInterests").assertIsDisplayed()

            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonHome").performClick()
            onNodeWithTag("topAppBarHome").assertIsDisplayed()
        }
    }

    @Test
    fun backPress_at_home_and_close_app() {
        // Test for the case when the home screen is shown by clicking on "home" on drawer
        // when the interests screen is being opened.
        with(composeTestRule) {
            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonInterests").performClick()

            onNodeWithTag("appDrawer").performClick()

            onNodeWithTag("appDrawerButtonHome").performClick()

            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()

            assert(activity.isFinishing)
        }
    }
}
