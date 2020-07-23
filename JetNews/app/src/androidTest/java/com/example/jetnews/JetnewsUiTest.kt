/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews

import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.hasSubstring
import androidx.ui.test.onAllNodes
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@MediumTest
@RunWith(JUnit4::class)
class JetnewsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Before
    fun setUp() {
        // Using targetContext as the Context of the instrumentation code
        composeTestRule.launchJetNewsApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun app_launches() {
        onNodeWithText("Jetnews").assertIsDisplayed()
    }

    @Test
    fun app_opensArticle() {
        // Using unmerged tree because of https://issuetracker.google.com/issues/161979921
        onAllNodes(hasSubstring("Manuel Vivo"), useUnmergedTree = true)[0].performClick()
        onAllNodes(hasSubstring("3 min read"), useUnmergedTree = true)[0].assertIsDisplayed()
    }
}
