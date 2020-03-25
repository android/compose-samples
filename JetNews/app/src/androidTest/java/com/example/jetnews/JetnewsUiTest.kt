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
import androidx.test.filters.Suppress
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.doClick
import androidx.ui.test.findByText
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@MediumTest
@RunWith(JUnit4::class)
class JetnewsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.launchJetNewsApp()
    }

    @Test
    fun avoidemptyTestSuite() {
        // this is an empty test to make gradle pass the suite
        // TODO(b/150728822): remove this after test runner is fixed
    }

    @Test
    @Suppress // TODO(b/150728822): re-enabled after test runner is fixed
    fun app_launches() {
        findByText("Jetnews").assertIsDisplayed()
    }

    @Test
    @Suppress // TODO(b/150728822): re-enabled after test runner is fixed
    fun app_opensArticle() {
        findAllBySubstring("Manuel Vivo").first().doClick()
        findAllBySubstring("July 30 • 3 min read").first().assertIsDisplayed()
    }
}
