/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import androidx.activity.ComponentActivity
import androidx.compose.Providers
import androidx.ui.geometry.Offset
import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.center
import androidx.ui.test.doClick
import androidx.ui.test.doGesture
import androidx.ui.test.findByLabel
import androidx.ui.test.findByText
import androidx.ui.test.sendSwipe
import androidx.ui.unit.milliseconds
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the features in the Conversation screen work as expected.
 */
class ConversationTest {

    @get:Rule
    val composeTestRule = AndroidComposeTestRule<NavActivity>()

    // Note that keeping these references is only safe if the activity is not recreated.
    // See: https://issuetracker.google.com/160862278
    private lateinit var activity: ComponentActivity

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity ->
            activity = newActivity
            // Launch the conversation screen
            composeTestRule.setContent {
                Providers(BackPressedDispatcherAmbient provides newActivity) {
                    JetchatTheme {
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
        findByText(activity.getString(R.string.textfield_hint)).assertIsDisplayed()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        // Check list is snapped to bottom and swipe up
        findJumpToBottom().assertDoesNotExist()
        findByLabel(activity.getString(R.string.conversation_desc)).doGesture {
            this.sendSwipe(
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
        findByLabel(activity.getString(R.string.conversation_desc)).doGesture {
            this.sendSwipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                duration = 200.milliseconds
            )
        }
        // Snap scroll to the bottom
        findJumpToBottom().doClick()

        // Check that the button is hidden
        findJumpToBottom().assertDoesNotExist()
    }

    private fun findJumpToBottom() = findByText(activity.getString(R.string.jumpBottom))
}
