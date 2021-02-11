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
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.center
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipe
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationTestTag
import com.example.compose.jetchat.conversation.LocalBackPressedDispatcher
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.WindowInsets
import kotlinx.coroutines.flow.MutableStateFlow
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
        // Provide empty insets. We can modify this value as necessary
        val windowInsets = WindowInsets()

        // Launch the conversation screen
        composeTestRule.setContent {
            val onBackPressedDispatcher = composeTestRule.activity.onBackPressedDispatcher
            Providers(
                LocalBackPressedDispatcher provides onBackPressedDispatcher,
                LocalWindowInsets provides windowInsets
            ) {
                JetchatTheme(isDarkTheme = themeIsDark.collectAsState(false).value) {
                    ConversationContent(
                        uiState = exampleUiState,
                        navigateToProfile = { },
                        onNavIconPressed = { }
                    )
                }
            }
        }
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
        composeTestRule.onNodeWithTag(ConversationTestTag).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200
            )
        }
        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        // When the scroll is not snapped to the bottom
        composeTestRule.onNodeWithTag(ConversationTestTag).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200
            )
        }
        // Snap scroll to the bottom
        findJumpToBottom().performClick()

        // Check that the button is hidden
        findJumpToBottom().assertDoesNotExist()
    }

    @Test
    fun jumpToBottom_snapsToBottomAfterUserInteracted() {
        // First swipe
        composeTestRule.onNodeWithTag(ConversationTestTag).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200
            )
        }
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
        composeTestRule.onNodeWithTag(ConversationTestTag).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 200
            )
        }

        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()

        // Set theme to dark
        themeIsDark.value = true

        // Check that the jump to bottom button is still shown
        findJumpToBottom().assertIsDisplayed()
    }

    private fun findJumpToBottom() =
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.jumpBottom))

    private fun openEmojiSelector() =
        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(R.string.emoji_selector_bt_desc)
            )
            .performClick()
}
