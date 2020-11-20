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
import androidx.compose.runtime.Composable
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.theme.JetnewsTheme

@Composable
fun JetnewsApp(
    appContainer: AppContainer,
    navigationViewModel: NavigationViewModel
) {
    JetnewsTheme {
        AppContent(
            navigationViewModel = navigationViewModel,
            interestsRepository = appContainer.interestsRepository,
            postsRepository = appContainer.postsRepository
        )
    }
}

@Composable
private fun AppContent(
    navigationViewModel: NavigationViewModel,
    postsRepository: PostsRepository,
    interestsRepository: InterestsRepository
) {
    Crossfade(navigationViewModel.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                is Screen.Home -> HomeScreen(
                    navigateTo = navigationViewModel::navigateTo,
                    postsRepository = postsRepository
                )
                is Screen.Interests -> InterestsScreen(
                    navigateTo = navigationViewModel::navigateTo,
                    interestsRepository = interestsRepository
                )
                is Screen.Article -> ArticleScreen(
                    postId = screen.postId,
                    postsRepository = postsRepository,
                    onBack = { navigationViewModel.onBack() }
                )
            }
        }
    }
}
