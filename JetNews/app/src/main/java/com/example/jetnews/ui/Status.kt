/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui

import androidx.compose.Model
import androidx.compose.frames.ModelList
import androidx.compose.frames.modelListOf

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen {
    object Home : Screen()
    data class Article(val postId: String) : Screen()
    object Interests : Screen()
    object Favorites : Screen()
}

@Model
object JetnewsStatus {
    var currentScreen: Screen = Screen.Home
    var backStack: ModelList<Screen> = ModelList()
    val favorites = ModelList<String>()
    val bookmarks = ModelList<String>()
    val selectedTopics = ModelList<String>()
}

/**
 * Temporary solution pending navigation support.
 */
fun navigateTo(destination: Screen, reset: Boolean = true) {
    if(reset) JetnewsStatus.backStack.clear()

    JetnewsStatus.backStack.add(destination)
    JetnewsStatus.currentScreen = destination
}

/**
 * Temporary solution to handle backpress
 */
fun backpress(){
    if(JetnewsStatus.backStack.isNotEmpty()) {
        JetnewsStatus.backStack.removeAt(JetnewsStatus.backStack.size - 1)
        JetnewsStatus.currentScreen = JetnewsStatus.backStack.lastOrNull() ?: Screen.Home
    }
}
