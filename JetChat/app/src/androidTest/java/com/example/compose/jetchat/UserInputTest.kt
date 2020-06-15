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

import androidx.compose.Providers
import androidx.test.espresso.Espresso
import androidx.ui.test.SemanticsMatcher
import androidx.ui.test.SemanticsNodeInteraction
import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertIsEnabled
import androidx.ui.test.assertIsNotEnabled
import androidx.ui.test.doClick
import androidx.ui.test.doSendText
import androidx.ui.test.find
import androidx.ui.test.findBySubstring
import androidx.ui.test.findByText
import androidx.ui.test.hasAnyAncestorThat
import androidx.ui.test.hasInputMethodsSupport
import androidx.ui.test.hasText
import com.example.compose.jetchat.conversation.BackPressedDispatcherAmbient
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.KeyboardShownKey
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the user input composable, including extended controls, behave as expected.
 */
class UserInputTest {

    @get:Rule
    val composeTestRule = AndroidComposeTestRule<NavActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            Providers(
                BackPressedDispatcherAmbient provides composeTestRule.activityTestRule.activity
            ) {
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

    @Test
    fun emojiSelector_isClosedWithBack() {
        // Click on text field
        findBySubstring(composeTestRule.getString(R.string.textfield_hint)).doClick()
        // Open emoji selector
        openEmojiSelector()
        // Check emoji selector is displayed
        assertEmojiSelectorIsDisplayed()
        // Press back button
        Espresso.pressBack()
        // Check the emoji selector is not displayed
        assertEmojiSelectorDoesNotExist()
    }

    @Test
    fun extendedUserInputShown_textFieldClicked_extendedUserInputHides() {
        openEmojiSelector()

        // Click on text field
        clickOnTextField()

        // Check the emoji selector is not displayed
        assertEmojiSelectorDoesNotExist()
    }

    /**
     * TODO: Flaky test:
     *    1/5 - ` Reason: Expected exactly '1' node but could not find any node
     *  that satisfies: (KeyboardShownKey = 'false')`
     *    1/20 - hitting r.android.com/1341840
     */
    @Test
    fun keyboardShown_emojiSelectorOpened_keyboardHides() {
        // Click on text field to open the soft keyboard
        clickOnTextField()
        find(SemanticsMatcher.expectValue(KeyboardShownKey, true)).assertExists()

        // When the emoji selector is extended
        openEmojiSelector()

        // Check that the keyboard is hidden
        find(SemanticsMatcher.expectValue(KeyboardShownKey, false)).assertExists()
    }

    @Test
    fun sendButton_enableToggles() {
        // Given an initial state where there's no text in the textfield,
        // check that the send button is disabled.
        findSendButton().assertIsNotEnabled()

        // Add some text to the input field
        findTextInputField().doSendText("Some text")

        // The send button should be enabled
        findSendButton().assertIsEnabled()
    }

    private fun clickOnTextField() {
        findByText(composeTestRule.getString(R.string.textfield_desc)).doClick()
    }

    private fun openEmojiSelector() =
        findByText(composeTestRule.getString(R.string.emoji_selector_bt_desc)).doClick()

    private fun assertEmojiSelectorIsDisplayed() =
        findByText(composeTestRule.getString(R.string.emoji_selector_desc)).assertIsDisplayed()

    private fun assertEmojiSelectorDoesNotExist() =
        findByText(composeTestRule.getString(R.string.emoji_selector_desc)).assertDoesNotExist()

    private fun findSendButton() = findByText(composeTestRule.getString(R.string.send))

    private fun findTextInputField(): SemanticsNodeInteraction {
        return find(
            hasInputMethodsSupport() and
                hasAnyAncestorThat(hasText(composeTestRule.getString(R.string.textfield_desc)))
        )
    }
}
