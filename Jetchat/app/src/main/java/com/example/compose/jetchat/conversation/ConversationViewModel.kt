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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.data.conversationUiState

/**
 * Holds the conversation state so it survives configuration changes, and handles message
 * actions such as liking a message.
 *
 * @param channel the channel of the conversation
 */
class ConversationViewModel(channel: String) : ViewModel() {

    var uiState by mutableStateOf(conversationUiState(channel))
        private set

    fun toggleLike(messageId: String) {
        uiState.toggleLike(messageId)
    }
}
