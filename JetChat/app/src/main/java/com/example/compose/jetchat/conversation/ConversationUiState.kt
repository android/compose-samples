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

package com.example.compose.jetchat.conversation

import androidx.compose.Immutable
import androidx.compose.frames.modelListOf
import com.example.compose.jetchat.R

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<Message>
) {
    private val _messages: MutableList<Message> = modelListOf(*initialMessages.toTypedArray())
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(msg)
    }
}

@Immutable
data class Message(val author: String, val content: String, val timestamp: String, val image: Int? = null)

val initialMessages = listOf(
    Message(
        "Yuri Gagarin",
        "This is Major Tom to Ground Control",
        "8:00 PM"
    ),
    Message(
        "Alan Shepard",
        "I'm stepping through the door",
        "8:01 PM"
    ),
    Message(
        "Virgil Grissom",
        "And I'm floating in a most peculiar way",
        "8:02 PM"
    ),
    Message(
        "Gherman Titov",
        "And the stars look very different today",
        "8:03 PM"
    ),
    Message(
        "John Glenn",
        "For here",
        "8:04 PM"
    ),
    Message(
        "Taylor Brooks",
        "@aliconnors Take a look at the `Flow.collectAsState()` APIs",
        "8:05 PM"
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        "8:05 PM"
    ),
    Message(
        "me",
        "Thank you!",
        "8:06 PM",
        R.drawable.sticker
    ),
    Message(
        "me",
        "Check it out!",
        "8:07 PM"
    )
)

val exampleUiState = ConversationUiState(
    initialMessages = initialMessages,
    channelName = "#composers",
    channelMembers = 42
)
