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

import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.theme.JetnewsTheme

@Composable
fun JetnewsApp(appContainer: AppContainer) {
    JetnewsTheme {
        AppContent(
            interestsRepository = appContainer.interestsRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@Composable
private fun AppContent(
    postsRepository: PostsRepository,
    interestsRepository: InterestsRepository
) {
    val navController = rememberNavController()
    val actions = remember(navController) { Actions(navController) }
    val scaffoldState = rememberScaffoldState()

    Crossfade(navController.currentBackStackEntryAsState()) {
        Surface(color = MaterialTheme.colors.background) {
            NavHost(navController, startDestination = ScreenName.HOME.name) {
                composable(ScreenName.HOME.name) {
                    HomeScreen(
                        navigateTo = actions.select,
                        postsRepository = postsRepository,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.INTERESTS.name) {
                    InterestsScreen(
                        navigateTo = actions.select,
                        interestsRepository = interestsRepository,
                        scaffoldState = scaffoldState
                    )
                }
                composable(ScreenName.ARTICLE.name + "/{${Screen.ArticleArgs.PostId}}") {
                    val postId =
                        requireNotNull(it.arguments?.getString(Screen.ArticleArgs.PostId))
                    ArticleScreen(
                        postId = postId,
                        postsRepository = postsRepository,
                        onBack = actions.upPress
                    )
                }
            }
        }
    }
}
