/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetsnack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.jetsnack.ui.JetsnackApp
import com.example.jetsnack.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            JetsnackApp()
        }
    }

    @Test
    fun app_launches() {
        // Check app launches at the correct destination
        composeTestRule.onNodeWithText("HOME").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android's picks").assertIsDisplayed()
    }

    @Test
    fun app_canNavigateToAllScreens() {
        // Check app launches at HOME
        composeTestRule.onNodeWithText("HOME").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android's picks").assertIsDisplayed()

        // Navigate to Search
        composeTestRule.onNodeWithText("SEARCH").performClick().assertIsDisplayed()
        composeTestRule.onNodeWithText("Categories").assertIsDisplayed()

        // Navigate to Cart
        composeTestRule.onNodeWithText("MY CART").performClick().assertIsDisplayed()
        composeTestRule.onNodeWithText("Order (3 items)").assertIsDisplayed()

        // Navigate to Profile
        composeTestRule.onNodeWithText("PROFILE").performClick().assertIsDisplayed()
        composeTestRule.onNodeWithText("This is currently work in progress").assertIsDisplayed()
    }

    @Test
    fun app_canNavigateToDetailPage() {
        composeTestRule.onNodeWithText("Chips").performClick()
        composeTestRule.onNodeWithText("Lorem ipsum", substring = true).assertIsDisplayed()
    }
}
