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

package com.example.jetnews.ui.navigation

import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.interests.InterestsKey
import com.example.jetnews.ui.post.PostKey

class Navigator(val state: NavigationState) {
    fun toHome() {
        if (state.topLevelRoute == HomeKey) return
        state.topLevelRoute = HomeKey
    }

    fun toPost(postId: String) {
        val postKey = PostKey(postId)
        if (state.topLevelRoute == HomeKey && state.backStacks[HomeKey]?.lastOrNull()?.equals(postKey) == true) return
        state.topLevelRoute = HomeKey
        state.backStacks[HomeKey]?.apply {
            if (getOrNull(1) == null) add(postKey) else set(1, postKey)
        }
    }

    fun toInterests() {
        if (state.topLevelRoute == InterestsKey) return
        state.topLevelRoute = InterestsKey
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.mainTopLevelRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
