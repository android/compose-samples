/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetnews.ui

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.example.jetnews.ui.Screen.Article
import com.example.jetnews.ui.ScreenName.ARTICLE
import com.example.jetnews.ui.ScreenName.HOME
import com.example.jetnews.ui.ScreenName.INTERESTS

/**
 * Screen names (used for serialization)
 */
enum class ScreenName { HOME, INTERESTS, ARTICLE }

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Screen(val id: ScreenName) {
    object Home : Screen(HOME)
    object Interests : Screen(INTERESTS)
    data class Article(val postId: String) : Screen(ARTICLE)

    object ArticleArgs {
        const val PostId = "postId"
    }
}

class Actions(navController: NavHostController) {
    val select: (Screen) -> Unit = { screen ->

        when (screen) {
            is Article -> {
                navController.navigate("${screen.id.name}/${screen.postId}")
            }
            else -> {
                navController.popBackStack(
                    navController.graph.startDestination,
                    navController.currentDestination?.id != navController.graph.startDestination
                )
                navController.navigate(screen.id.name)
            }
        }
    }

    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}
