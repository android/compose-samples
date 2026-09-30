/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat.conversation

import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.data.initialMessages

/**
 * Holds the conversation state so it survives configuration changes, and handles message
 * actions such as liking a message.
 */
class ConversationViewModel : ViewModel() {

    val uiState = ConversationUiState(
        channelName = exampleUiState.channelName,
        channelMembers = exampleUiState.channelMembers,
        initialMessages = initialMessages,
    )

    fun toggleLike(messageId: String) {
        uiState.toggleLike(messageId)
    }
}
