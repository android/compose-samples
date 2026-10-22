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

package com.example.compose.jetchat.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The list of chats and recent profiles, shown in the list pane of the list-detail layout.
 *
 * The keys are [Serializable] and implement [NavKey] so that the back stack created with
 * `rememberNavBackStack` survives configuration changes and process death.
 */
@Serializable
data object ChatList : NavKey

/** The conversation screen of [channel]. */
@Serializable
data class Conversation(val channel: String) : NavKey

/** The profile screen of the user identified by [userId]. */
@Serializable
data class Profile(val userId: String, val isExtraPane: Boolean = false) : NavKey

/** The full-screen video player for [videoUri] with optional [messageText] caption. */
@Serializable
data class VideoPlayer(val videoUri: String, val messageText: String? = null) : NavKey

/**
 * Opens the conversation of [channel]. Only keeps [ChatList] and the selected conversation so
 * that profiles opened before are popped off the back stack.
 */
fun NavBackStack<NavKey>.openConversation(channel: String) {
    val conversation = Conversation(channel)
    removeAll { it != ChatList && it != conversation }
    if (lastOrNull() != conversation) {
        add(conversation)
    }
}

/**
 * Opens the profile of the user identified by [userId] on top of the current screen, replacing
 * any profile that is already shown instead of stacking profiles.
 */
fun NavBackStack<NavKey>.openProfile(userId: String, isExtraPane: Boolean = false) {
    val profile = Profile(userId, isExtraPane)
    when (lastOrNull()) {
        profile -> Unit
        is Profile -> this[lastIndex] = profile
        else -> add(profile)
    }
}
