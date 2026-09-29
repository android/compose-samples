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

package com.example.compose.jetchat

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.Espresso
import com.example.compose.jetchat.conversation.ConversationTestTag
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavActivity>()

    @Test
    fun app_launches() {
        // Check app launches at the correct destination
        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        // Navigate to profile
        navigateToProfile("Taylor Brooks")
        // Extra UI check (scroll to Display name as the new profile header is taller)
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .performScrollTo()
            .assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home
        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    @Test
    fun messageAuthor_click_navigatesToProfile_and_back() {
        composeTestRule
            .onAllNodes(hasContentDescription("Taylor Brooks"))
            .onFirst()
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .performScrollTo()
            .assertIsDisplayed()

        Espresso.pressBack()

        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    /**
     * Regression test for https://github.com/android/compose-samples/issues/670
     */
    @Test
    fun drawer_conversationScreen_backstackPopUp() {
        navigateToProfile("Ali Conors (you)")
        navigateToHomeFromProfile()
        navigateToProfile("Taylor Brooks")
        navigateToHomeFromProfile()

        // Chewie, we're home
        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    private fun navigateToProfile(name: String) {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.navigation_drawer_open),
        ).performClick()

        composeTestRule.onNode(hasText(name) and isInDrawer()).performClick()
    }

    private fun isInDrawer() = hasAnyAncestor(isDrawer())

    private fun isDrawer() = SemanticsMatcher.expectValue(
        SemanticsProperties.PaneTitle,
        composeTestRule.activity.getString(androidx.compose.ui.R.string.navigation_menu),
    )

    private fun navigateToHomeFromProfile() {
        composeTestRule.onAllNodes(isRoot()).onFirst().performTouchInput {
            swipeRight()
        }
        composeTestRule.onNode(hasText("composers") and isInDrawer()).performClick()
    }
}
