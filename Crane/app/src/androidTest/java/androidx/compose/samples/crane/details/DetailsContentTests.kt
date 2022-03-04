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

package androidx.compose.samples.crane.details

import androidx.compose.material.Surface
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.data.MADRID
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsContentTests {
    private val city = MADRID
    private val testExploreModel = ExploreModel(city, "description", "imageUrl")

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            CraneTheme {
                Surface {
                    DetailsContent(exploreModel = testExploreModel)
                }
            }
        }
    }

    @Test
    fun detailsScreen_detailsDisplayed() {
        composeTestRule.onNodeWithText(city.nameToDisplay).assertIsDisplayed()
        composeTestRule.onNodeWithText(testExploreModel.description).assertIsDisplayed()
    }
}
