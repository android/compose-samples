/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.utils.WindowSize
import com.example.jetnews.utils.getWindowSize

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    showNavRail: Boolean,
    openDrawer: () -> Unit,
    navigateToInterests: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        showNavRail = showNavRail,
        onToggleFavorite = { homeViewModel.toggleFavourite(it) },
        onSelectPost = { homeViewModel.selectArticle(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        onInteractWithList = { homeViewModel.interactedWithList() },
        onInteractWithDetail = { homeViewModel.interactedWithDetail(it) },
        openDrawer = openDrawer,
        navigateToInterests = navigateToInterests,
        scaffoldState = scaffoldState,
    )
}

/**
 * Displays the Home route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param onToggleFavorite (event) toggles favorite for a post
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) error message was shown
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    showNavRail: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithList: () -> Unit,
    onInteractWithDetail: (String) -> Unit,
    openDrawer: () -> Unit,
    navigateToInterests: () -> Unit,
    scaffoldState: ScaffoldState
) {
    // Construct the lazy list states for the list and the details outside of deciding which one to
    // show.
    // This allows the associated state to survive beyond that decision, and therefore we get to
    // preserve the scroll throughout any changes to the content.
    val homeListLazyListState = rememberLazyListState()
    val articleDetailLazyListStates = when (uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    }.associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }

    BoxWithConstraints {
        val useListDetail = when (getWindowSize(maxWidth)) {
            WindowSize.Compact, WindowSize.Medium -> false
            WindowSize.Expanded -> true
        }

        // Determine which type of the home screen to display
        val homeScreenType = if (useListDetail) {
            HomeScreenType.ListArticle
        } else {
            when (uiState) {
                is HomeUiState.HasPosts -> if (uiState.isArticleOpen) {
                    HomeScreenType.Article
                } else {
                    HomeScreenType.List
                }
                is HomeUiState.NoPosts -> HomeScreenType.List
            }
        }

        when (homeScreenType) {
            HomeScreenType.ListArticle -> {
                HomeListDetailScreen(
                    uiState = uiState,
                    showNavRail = showNavRail,
                    onToggleFavorite = onToggleFavorite,
                    onSelectPost = onSelectPost,
                    onRefreshPosts = onRefreshPosts,
                    onErrorDismiss = onErrorDismiss,
                    onInteractWithList = onInteractWithList,
                    onInteractWithDetail = onInteractWithDetail,
                    openDrawer = openDrawer,
                    navigateToInterests = navigateToInterests,
                    homeListLazyListState = homeListLazyListState,
                    articleDetailLazyListStates = articleDetailLazyListStates,
                    scaffoldState = scaffoldState
                )
            }
            HomeScreenType.List -> {
                HomeListScreen(
                    uiState = uiState,
                    showNavRail = showNavRail,
                    onToggleFavorite = onToggleFavorite,
                    onSelectPost = onSelectPost,
                    onRefreshPosts = onRefreshPosts,
                    onErrorDismiss = onErrorDismiss,
                    onInteractWithList = onInteractWithList,
                    openDrawer = openDrawer,
                    navigateToInterests = navigateToInterests,
                    homeListLazyListState = homeListLazyListState,
                    scaffoldState = scaffoldState
                )
            }
            HomeScreenType.Article -> {
                // Guaranteed by above condition
                check(uiState is HomeUiState.HasPosts)

                ArticleScreen(
                    post = uiState.selectedPost,
                    showNavRail = showNavRail,
                    onBack = onInteractWithList,
                    isFavorite = uiState.favorites.contains(uiState.selectedPost.id),
                    onToggleFavorite = {
                        onToggleFavorite(uiState.selectedPost.id)
                    },
                    lazyListState = articleDetailLazyListStates.getValue(
                        uiState.selectedPost.id
                    ),
                )

                // If we are just showing the detail, have a back press switch to the list.
                BackHandler {
                    onInteractWithList()
                }
            }
        }
    }
}

private enum class HomeScreenType {
    ListArticle,
    List,
    Article
}
