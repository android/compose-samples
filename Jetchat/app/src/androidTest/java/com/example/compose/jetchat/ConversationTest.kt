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
import androidx.compose.ui.unit.milliseconds
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.center
import androidx.ui.test.createAndroidComposeRule
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import androidx.ui.test.performGesture
import androidx.ui.test.swipe
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the features in the Conversation screen work as expected.
 */
class ConversationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NavActivity>(disableTransitions = true)

    // Note that keeping these references is only safe if the activity is not recreated.
    // See: https://issuetracker.google.com/160862278
    private lateinit var activity: ComponentActivity

    private val themeIsDark = MutableStateFlow(false)

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity ->
            activity = newActivity
            // Launch the conversation screen
            composeTestRule.setContent {
                Providers(BackPressedDispatcherAmbient provides newActivity) {
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
    }

    @Test
    fun app_launches() {
        // Check that the conversation screen is visible on launch
        composeTestRule.onNodeWithText(activity.getString(R.string.textfield_hint)).assertIsDisplayed()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        // Check list is snapped to bottom and swipe up
        findJumpToBottom().assertDoesNotExist()
        composeTestRule.onNodeWithLabel(activity.getString(R.string.conversation_desc)).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                duration = 200.milliseconds
            )
        }
        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        // When the scroll is not snapped to the bottom
        composeTestRule.onNodeWithLabel(activity.getString(R.string.conversation_desc)).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                duration = 200.milliseconds
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
        composeTestRule.onNodeWithLabel(activity.getString(R.string.conversation_desc)).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                duration = 200.milliseconds
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
        composeTestRule.onNodeWithLabel(activity.getString(R.string.conversation_desc)).performGesture {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                duration = 200.milliseconds
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
        composeTestRule.onNodeWithText(activity.getString(R.string.jumpBottom))

    private fun openEmojiSelector() =
        composeTestRule.onNodeWithLabel(activity.getString(R.string.emoji_selector_bt_desc)).performClick()
}
