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

import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.center
import androidx.ui.test.doClick
import androidx.ui.test.doGesture
import androidx.ui.test.findByText
import androidx.ui.test.sendSwipe
import androidx.ui.unit.PxPosition
import androidx.ui.unit.milliseconds
import androidx.ui.unit.px
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the features in the Conversation screen work as expected.
 */
class ConversationTest {

    @get:Rule
    val composeTestRule = AndroidComposeTestRule<NavActivity>()

    @Before
    fun setUp() {
        // Launch the conversation screen
        composeTestRule.startConversationScreen()
    }

    @Test
    fun app_launches() {
        // Check that the conversation screen is visible on launch
        findByText(composeTestRule.getString(R.string.textfield_hint)).assertIsDisplayed()
    }

    @Test
    fun userScrollsUp_jumpToBottomAppears() {
        // Check list is snapped to bottom and swipe up
        findJumpToBottom().assertDoesNotExist()
        findByText(composeTestRule.getString(R.string.conversation_desc)).doGesture {
            this.sendSwipe(
                start = this.center,
                end = PxPosition(this.center.x, this.center.y + 500.px),
                duration = 100.milliseconds
            )
        }
        // Check that the jump to bottom button is shown
        findJumpToBottom().assertIsDisplayed()
    }

    @Test
    fun jumpToBottom_snapsToBottomAndDisappears() {
        // When the scroll is not snapped to the bottom
        findByText(composeTestRule.getString(R.string.conversation_desc)).doGesture {
            this.sendSwipe(
                start = this.center,
                end = PxPosition(this.center.x, this.center.y + 500.px),
                duration = 100.milliseconds
            )
        }
        // Snap scroll to the bottom
        findJumpToBottom().doClick()

        // Check that the button is hidden
        findJumpToBottom().assertDoesNotExist()
    }
    private fun findJumpToBottom() = findByText(composeTestRule.getString(R.string.jumpBottom))
}
