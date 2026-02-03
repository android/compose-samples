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

package com.example.jetnews.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.interests.InterestsViewModel

@Composable
fun JetnewsNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    backStack: SnapshotStateList<JetnewsRoute>,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit = {},
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { route ->
            when (route) {
                is Home -> NavEntry(route) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.provideFactory(
                            postsRepository = appContainer.postsRepository,
                            preSelectedPostId = route.preSelectedPostId,
                        ),
                    )
                    HomeRoute(
                        homeViewModel = homeViewModel,
                        isExpandedScreen = isExpandedScreen,
                        openDrawer = openDrawer,
                    )
                }
                is Interests -> NavEntry(route) {
                    val interestsViewModel: InterestsViewModel = viewModel(
                        factory = InterestsViewModel.provideFactory(appContainer.interestsRepository),
                    )
                    InterestsRoute(
                        interestsViewModel = interestsViewModel,
                        isExpandedScreen = isExpandedScreen,
                        openDrawer = openDrawer,
                    )
                }
            }
        },
    )
}
