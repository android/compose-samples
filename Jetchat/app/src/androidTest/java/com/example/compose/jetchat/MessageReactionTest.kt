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
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.compose.jetchat.conversation.ChatItemBubble
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationTestTag
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.EmojiPickerDialog
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.conversation.Messages
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.data.initialMessages
import com.example.compose.jetchat.theme.JetchatTheme
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageReactionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    //Test 1 : Emoji picker dialog shows with correct test tag
    @Test
    fun emojiPickerDialog_isDisplayed_withCorrectTag(){
        composeTestRule.setContent {
            JetchatTheme {
                EmojiPickerDialog(onEmojiSelected = {}, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithTag("reaction_picker").assertIsDisplayed()

    }

    //Test 2 : Emoji picker show all emoji options row
    @Test
    fun emojiPickerDialog_showEmojiRow(){
        composeTestRule.setContent {
            JetchatTheme {
                EmojiPickerDialog(onEmojiSelected = {}, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithTag("reaction_emoji_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emoji_option_👍").assertIsDisplayed()
    }

    //Test 3 : Tapping emoji triggers callback with correct value
    @Test
    fun emojiPickerDialog_selectEmoji_triggersCallback(){
        var result : String? = null

        composeTestRule.setContent {
            JetchatTheme {
                EmojiPickerDialog(onEmojiSelected = { result = it}, onDismiss = {})
            }
        }
        composeTestRule.onNodeWithTag("emoji_option_👍").performClick()
        assertEquals("👍",result)
    }

    //Test 4 : Long press on message bubble shows emoji picker
    @Test
    fun chatBubble_longPress_showsEmojiPicker(){
        composeTestRule.setContent {
            JetchatTheme {
                ChatItemBubble(
                    message = Message(author = "Taylor", content = "Hello!", timestamp = "8:00 PM"),
                    isUserMe = false,
                    authorClicked = {},
                    onReactionAdded = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("Hello!").performTouchInput { longClick() }
        composeTestRule.onNodeWithTag("reaction_picker").assertIsDisplayed()
    }

    //Test 5 : Reaction chip appears when message has reaction
    @Test
    fun chatBubble_withReaction_showsReactionClip(){
        composeTestRule.setContent {
            JetchatTheme {
                ChatItemBubble(
                    message = Message(
                        author = "Taylor",
                        content = "Hello!",
                        timestamp = "8:00 PM",
                        reactions = mapOf("👍" to 1)
                    ),
                    isUserMe = false,
                    authorClicked = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("reaction_chip_👍").assertIsDisplayed()
    }

    //Test 6 : addReaction increments count correctly
    @Test
    fun conversationUiState_addReaction_incrementsCount(){
        val state = ConversationUiState(
            channelName = "#test",
            channelMembers =  2 ,
            initialMessages = listOf(
                Message(author = "me", content = "Hi", timestamp = "8:00 PM")
            )
        )
        state.addReaction(0, "👍")
        assertEquals(1, state.messages[0].reactions["👍"])
        state.addReaction(0, "👍")
        assertEquals(2, state.messages[0].reactions["👍"])
    }

    //Test 7 : Multiple different reactions tacked separately
    @Test
    fun conversationUiState_multipleReaction_trackedSeparately(){
        val state = ConversationUiState(
            channelName = "#test",
            channelMembers =  2 ,
            initialMessages = listOf(
                Message(author = "me", content = "Hi", timestamp = "8:00 PM")
            )
        )
        state.addReaction(0, "👍")
        state.addReaction(0, "❤️")
        state.addReaction(0, "👍")
        assertEquals(2, state.messages[0].reactions["👍"])
        assertEquals(1, state.messages[0].reactions["❤️"])
    }


}