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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSubstring
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@MediumTest
class JetnewsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Using targetContext as the Context of the instrumentation code
        composeTestRule.launchJetNewsApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Ignore("TODO Investigate why this passes locally but fail on CI")
    @Test
    fun app_launches() {
        composeTestRule.onNodeWithText("Jetnews").assertIsDisplayed()
    }

    @Ignore("TODO Investigate why this passes locally but fail on CI")
    @Test
    fun app_opensArticle() {
        composeTestRule.onAllNodes(hasSubstring("Manuel Vivo"))[0].performClick()
        composeTestRule.onAllNodes(hasSubstring("3 min read"))[0].assertIsDisplayed()
    }
}
