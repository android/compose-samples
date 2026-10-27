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

import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationTestTag
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.data.resetFakeData
import com.example.compose.jetchat.theme.JetchatTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the features in the Conversation screen work as expected.
 */
class ConversationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val themeIsDark = MutableStateFlow(false)

    @Before
    fun setUp() {
        resetFakeData()
        val uiState = createConversationTestUiState()
        // Launch the conversation screen
        composeTestRule.setContent {
            JetchatTheme(isDarkTheme = themeIsDark.collectAsStateWithLifecycle(false).value) {
                ConversationContent(
                    uiState = uiState,
                    navigateToProfile = { },
                    onNavIconPressed = { },
                )
            }
        }
    }

    @After
    fun tearDown() {
        resetFakeData()
    }

    @Test
    fun app_launches() {
        // Check that the conversation screen is visible on launch
        composeTestRule.onNodeWithTag(ConversationTestTag).assertIsDisplayed()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        // Check list is snapped to bottom and swipe up
        findJumpToBottom().assertDoesNotExist()
        scrollAwayFromBottom()
        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        // When the scroll is not snapped to the bottom
        scrollAwayFromBottom()
        // Snap scroll to the bottom
        findJumpToBottom().performClick()

        // Check that the button is hidden
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun jumpToBottom_snapsToBottomAfterUserInteracted() {
        // First swipe
        scrollAwayFromBottom()
        // Second, snap to bottom
        findJumpToBottom().performClick()

        // Open Emoji selector
        openEmojiSelector()

        // Assert that the list is still snapped to bottom
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun changeTheme_scrollIsPersisted() {
        // Swipe to show the jump to bottom button
        scrollAwayFromBottom()

        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()

        // Set theme to dark
        themeIsDark.value = true

        // Check that the jump to bottom button is still shown
        findJumpToBottom().assertIsDisplayed()
    }

    /**
     * Scrolls the conversation far enough away from the bottom (more than 1.5x the viewport
     * height) for the jump to bottom button to appear. Start from the center so the touch down
     * does not land on the overlaid top app bar.
     */
    private fun scrollAwayFromBottom() {
        repeat(5) {
            composeTestRule.onNodeWithTag(
                testTag = ConversationTestTag,
                useUnmergedTree = true, // https://issuetracker.google.com/issues/184825850
            ).performTouchInput {
                this.swipe(
                    start = this.center,
                    end = Offset(this.center.x, this.bottom - 10f),
                    durationMillis = 200,
                )
            }
            composeTestRule.waitForIdle()
        }
    }

    private fun findJumpToBottom() = composeTestRule.onNodeWithText(
        composeTestRule.activity.getString(R.string.jumpBottom),
        useUnmergedTree = true,
    )

    private fun openEmojiSelector() = composeTestRule
        .onNodeWithContentDescription(
            label = composeTestRule.activity.getString(R.string.emoji_selector_bt_desc),
            useUnmergedTree = true, // https://issuetracker.google.com/issues/184825850
        )
        .performClick()
}

/**
 * Make the list of messages longer so the test makes sense on tablets.
 */
private fun createConversationTestUiState() = ConversationUiState(
    initialMessages = exampleUiState.messages +
        exampleUiState.messages.map { it.copy(id = "${it.id}-copy") },
    channelName = "#composers",
    channelMembers = 42,
)
