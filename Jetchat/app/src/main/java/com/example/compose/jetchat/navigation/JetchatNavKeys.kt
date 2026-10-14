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

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The conversation screen, which is the start destination.
 *
 * The keys are [Serializable] and implement [NavKey] so that the back stack created with
 * `rememberNavBackStack` survives configuration changes and process death.
 */
@Serializable
data object Conversation : NavKey

/** The profile screen of the user identified by [userId]. */
@Serializable
data class Profile(val userId: String) : NavKey
