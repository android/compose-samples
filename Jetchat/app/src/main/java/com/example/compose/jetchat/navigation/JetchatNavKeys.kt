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
 * The conversation screen of [channel], which is the start destination and always the root of the
 * back stack.
 *
 * The keys are [Serializable] and implement [NavKey] so that the back stack created with
 * `rememberNavBackStack` survives configuration changes and process death.
 */
@Serializable
data class Conversation(val channel: String) : NavKey

/** The profile screen of the user identified by [userId]. */
@Serializable
data class Profile(val userId: String) : NavKey

/**
 * Opens the conversation of [channel]. The conversation is always the root of the back stack, so
 * this replaces the current conversation and pops everything above it.
 */
fun NavBackStack<NavKey>.openConversation(channel: String) {
    removeAll { it !is Conversation }
    this[0] = Conversation(channel)
}

/**
 * Opens the profile of the user identified by [userId] on top of the current screen. The same
 * profile is never stacked twice, e.g. when an avatar is tapped repeatedly.
 */
fun NavBackStack<NavKey>.openProfile(userId: String) {
    val profile = Profile(userId)
    if (lastOrNull() != profile) {
        add(profile)
    }
}
