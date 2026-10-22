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
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.Espresso
import org.junit.Assert.assertTrue
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
        assertConversationIsDisplayed()
    }

    @Test
    fun profileScreen_back_conversationScreen() {
        // Navigate to profile
        navigateToProfile("Taylor Brooks")
        // Check profile is displayed (scroll to Display name as the new profile header is taller)
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .performScrollTo()
            .assertIsDisplayed()

        // Press back
        Espresso.pressBack()

        // Check that we're home and the profile was popped
        assertConversationIsDisplayed()
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.display_name))
            .assertDoesNotExist()
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
        assertConversationIsDisplayed()

        // The profiles were popped off the back stack, so pressing back leaves the app
        val activity = composeTestRule.activity
        Espresso.pressBackUnconditionally()
        composeTestRule.runOnIdle {
            assertTrue(activity.isFinishing)
        }
    }

    private fun assertConversationIsDisplayed() {
        // The channel name is shown in the conversation's app bar (and in the chat list, which
        // is excluded)
        composeTestRule.onNode(hasText("composers") and !isInAnyChatList()).assertIsDisplayed()
    }

    private fun navigateToProfile(name: String) {
        if (!chatListIsPane) {
            composeTestRule.onNodeWithContentDescription(
                composeTestRule.activity.getString(R.string.navigation_drawer_open),
            ).performClick()
        }

        composeTestRule.onNode(hasText(name) and isInChatList()).performClick()
    }

    private fun navigateToHomeFromProfile() {
        if (chatListIsPane) {
            // On two-pane windows the profile hides the list pane; the channel name brings it
            // back. On wider windows the list pane stays next to the conversation.
            if (!isListPaneDisplayed()) {
                composeTestRule.onNodeWithContentDescription(
                    composeTestRule.activity.getString(R.string.show_chat_list),
                ).performClick()
            }
        } else {
            composeTestRule.onAllNodes(isRoot()).onFirst().performTouchInput {
                swipeRight()
            }
        }
        composeTestRule.onNode(hasText("composers") and isInChatList()).performClick()
    }

    /**
     * Whether the chat list is shown in the list pane next to the conversation (wide windows)
     * rather than in the navigation drawer (single-pane windows).
     *
     * Resolved on first use, which must happen while the conversation is on top of the back
     * stack: on two-pane windows a profile hides the list pane.
     */
    private val chatListIsPane: Boolean by lazy { isListPaneDisplayed() }

    private fun isListPaneDisplayed(): Boolean = try {
        composeTestRule.onNode(isListPane()).assertIsDisplayed()
        true
    } catch (e: AssertionError) {
        false
    }

    /**
     * Matches nodes in the container that currently shows the chat list. The closed drawer keeps
     * its content composed on wide windows, so the two containers are never matched together.
     */
    private fun isInChatList() = hasAnyAncestor(if (chatListIsPane) isListPane() else isDrawer())

    private fun isInAnyChatList() = hasAnyAncestor(isDrawer() or isListPane())

    private fun isDrawer() = SemanticsMatcher.expectValue(
        SemanticsProperties.PaneTitle,
        composeTestRule.activity.getString(androidx.compose.ui.R.string.navigation_menu),
    )

    private fun isListPane() = SemanticsMatcher.expectValue(
        SemanticsProperties.PaneTitle,
        composeTestRule.activity.getString(R.string.chat_list),
    )
}
