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
import androidx.compose.Model
import androidx.compose.frames.modelListOf
import com.example.compose.jetchat.R

@Model
data class ConversationUiState(
    private val _messages: MutableList<Message> = modelListOf(*initialMessages.toTypedArray()),
    val messages: List<Message> = _messages
) {
    fun addMessage(msg: Message) {
        _messages.add(msg)
    }
}

@Immutable
data class Message(val author: String, val content: String, val image: Int? = null)

val initialMessages = listOf(
    Message(
        "Yuri Gagarin",
        "This is Major Tom to Ground Control"
    ),
    Message(
        "Alan Shepard",
        "I'm stepping through the door"
    ),
    Message(
        "Virgil Grissom",
        "And I'm floating in a most peculiar way"
    ),
    Message(
        "Gherman Titov",
        "And the stars look very different today"
    ),
    Message("John Glenn", "For here"),
    Message(
        "Scott Carpenter",
        "Am I sitting in a tin can"
    ),
    Message(
        "Andriyan Nikolayev",
        "Far above the world"
    ),
    Message(
        "Pavel Popovich",
        "Planet Earth is blue",
        R.drawable.ic_crane_drawer
    ),
    Message(
        "Walter Schirra",
        "And there's nothing I can do"
    )
)
