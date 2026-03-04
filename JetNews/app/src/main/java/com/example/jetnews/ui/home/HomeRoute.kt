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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.navigation.ListDetailScene
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

fun EntryProviderScope<NavKey>.homeEntry(
    postsRepository: PostsRepository,
    isExpandedScreen: () -> Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
) {
    entry<HomeKey>(
        metadata = ListDetailScene.list(
            ListDetailScene.ListConfiguration(
                modifier = Modifier.width(334.dp),
                detailPlaceholder = {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Select an article",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                },
            ),
        ),
    ) {
        val homeViewModel: HomeViewModel =
            viewModel(factory = HomeViewModel.provideFactory(postsRepository))

        HomeRoute(
            homeViewModel = homeViewModel,
            isExpandedScreen = isExpandedScreen(),
            openDrawer = openDrawer,
            navigateToPost = navigateToPost,
        )
    }
}

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    HomeFeedScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavourite(it) },
        onSelectPost = { navigateToPost(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openDrawer = openDrawer,
        homeListLazyListState = rememberLazyListState(),
        snackbarHostState = snackbarHostState,
        onSearchInputChanged = { homeViewModel.onSearchInputChanged(it) },
        searchInput = uiState.searchInput,
    )
}
