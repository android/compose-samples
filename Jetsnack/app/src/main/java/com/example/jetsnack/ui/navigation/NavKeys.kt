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

package com.example.jetsnack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.jetsnack.ui.home.HomeSections
import kotlinx.serialization.Serializable

@Serializable
data object FeedKey : NavKey

@Serializable
data object SearchKey : NavKey

@Serializable
data object CartKey : NavKey

@Serializable
data object ProfileKey : NavKey

@Serializable
data class SnackDetailKey(val snackId: Long, val origin: String) : NavKey

fun NavBackStack<NavKey>.addHomeSection(key: NavKey) {
    // Remove everything except the Feed from the back stack.
    removeAll { it !is FeedKey }
    // Now add the key if it's not the Feed.
    if (key !is FeedKey) {
        add(key)
    }
}

fun NavBackStack<NavKey>.currentHomeSectionKey(): NavKey = findLast { it in HomeSections.routes }
    ?: error("No HomeSection key found in the back stack")

@Composable
fun NavBackStack<NavKey>.addSnackDetail(): (snackId: Long, origin: String) -> Unit {
    val lifecycleOwner = LocalLifecycleOwner.current
    return { snackId, origin ->
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            add(SnackDetailKey(snackId, origin))
        }
    }
}
