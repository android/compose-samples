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
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.KeyboardShownKey
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the user input composable, including extended controls, behave as expected.
 */
class UserInputTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val activity by lazy { composeTestRule.activity }

    @Before
    fun setUp() {
        // Launch the conversation screen
        composeTestRule.setContent {
            JetchatTheme {
                ConversationContent(
                    uiState = exampleUiState,
                    navigateToProfile = { },
                    onNavIconPressed = { }
                )
            }
        }
    }

    @Test
    @Ignore("Issue with keyboard sync https://issuetracker.google.com/169235317")
    fun emojiSelector_isClosedWithBack() {
        // Open emoji selector
        openEmojiSelector()
        // Check emoji selector is displayed
        assertEmojiSelectorIsDisplayed()

        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false))
            .assertExists()
        // Press back button
        Espresso.pressBack()

        // TODO: Workaround for synchronization issue with "back"
        // https://issuetracker.google.com/169235317
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithContentDescription(activity.getString(R.string.emoji_selector_desc))
                .fetchSemanticsNodes().isEmpty()
        }

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

        // TODO: Soft keyboard is not correctly synchronized
        //  https://issuetracker.google.com/169235317
        Thread.sleep(200)

        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, true)).assertExists()

        // When the emoji selector is extended
        openEmojiSelector()

        // Check that the keyboard is hidden
        composeTestRule.onNode(SemanticsMatcher.expectValue(KeyboardShownKey, false)).assertExists()
    }

    @Test
    @Ignore("Flaky due to https://issuetracker.google.com/169235317")
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
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.textfield_desc))
            .performClick()

    private fun openEmojiSelector() =
        composeTestRule
            .onNodeWithContentDescription(
                label = activity.getString(R.string.emoji_selector_bt_desc),
                useUnmergedTree = true // https://issuetracker.google.com/issues/184825850
            )
            .performClick()

    private fun assertEmojiSelectorIsDisplayed() =
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.emoji_selector_desc))
            .assertIsDisplayed()

    private fun assertEmojiSelectorDoesNotExist() =
        composeTestRule
            .onNodeWithContentDescription(activity.getString(R.string.emoji_selector_desc))
            .assertDoesNotExist()

    private fun findSendButton() = composeTestRule.onNodeWithText(activity.getString(R.string.send))

    private fun findTextInputField(): SemanticsNodeInteraction {
        return composeTestRule.onNode(
            hasSetTextAction() and
                hasAnyAncestor(hasContentDescription(activity.getString(R.string.textfield_desc)))
        )
    }
}
