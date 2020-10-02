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
import androidx.test.espresso.Espresso
import androidx.ui.test.SemanticsMatcher
import androidx.ui.test.SemanticsNodeInteraction
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertIsEnabled
import androidx.ui.test.assertIsNotEnabled
import androidx.ui.test.createAndroidComposeRule
import androidx.ui.test.hasAnyAncestor
import androidx.ui.test.hasInputMethodsSupport
import androidx.ui.test.hasLabel
import androidx.ui.test.onNodeWithLabel
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import androidx.ui.test.performTextInput
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
    val composeTestRule = createAndroidComposeRule<NavActivity>(disableTransitions = true)

    // Note that keeping these references is only safe if the activity is not recreated.
    // See: https://issuetracker.google.com/160862278
    private lateinit var activity: ComponentActivity

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity ->
            activity = newActivity
            // Launch the conversation screen
            composeTestRule.setContent {
                Providers(BackPressedDispatcherAmbient provides activity) {
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
    fun emojiSelector_isClosedWithBack() {
        // Open emoji selector
        openEmojiSelector()
        // Check emoji selector is displayed
        assertEmojiSelectorIsDisplayed()

        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false))
            .assertExists()
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

    @Test
    fun keyboardShown_emojiSelectorOpened_keyboardHides() {
        // Click on text field to open the soft keyboard
        clickOnTextField()

        // TODO: Soft keyboard is not correctly synchronized https://issuetracker.google.com/169235317
        Thread.sleep(200)

        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, true)).assertExists()

        // When the emoji selector is extended
        openEmojiSelector()

        // Check that the keyboard is hidden
        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false)).assertExists()
    }

    @Test
    fun sendButton_enableToggles() {
        // Given an initial state where there's no text in the textfield,
        // check that the send button is disabled.
        findSendButton().assertIsNotEnabled()

        // Add some text to the input field
        findTextInputField().performTextInput("Some text")

        // The send button should be enabled
        findSendButton().assertIsEnabled()
    }

    private fun clickOnTextField() =
        composeTestRule.onNodeWithLabel(activity.getString(R.string.textfield_desc))
            .performClick()

    private fun openEmojiSelector() =
        composeTestRule.onNodeWithLabel(activity.getString(R.string.emoji_selector_bt_desc))
            .performClick()

    private fun assertEmojiSelectorIsDisplayed() =
        composeTestRule.onNodeWithLabel(activity.getString(R.string.emoji_selector_desc))
            .assertIsDisplayed()

    private fun assertEmojiSelectorDoesNotExist() =
        composeTestRule.onNodeWithLabel(activity.getString(R.string.emoji_selector_desc))
            .assertDoesNotExist()

    private fun findSendButton() = composeTestRule.onNodeWithText(activity.getString(R.string.send))

    private fun findTextInputField(): SemanticsNodeInteraction {
        return composeTestRule.onNode(
            hasInputMethodsSupport() and
                hasAnyAncestor(hasLabel(activity.getString(R.string.textfield_desc)))
        )
    }
}
