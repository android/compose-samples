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

package com.example.jetnews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.interests.InterestsViewModel

const val POST_ID = "postId"

@Composable
fun JetnewsNavDisplay(
    navigationState: NavigationState,
    appContainer: AppContainer,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit = {},
) {

    val decoratedEntries = navigationState.getEntries(
        entryProvider = entryProvider {
            homeEntry(
                appContainer.postsRepository,
                isExpandedScreen,
                openDrawer,
            )
            interestsEntry(
                appContainer.interestsRepository,
                isExpandedScreen,
                openDrawer,
            )
        },
    )

    NavDisplay(
        entries = decoratedEntries,
        modifier = modifier,
        onBack = onBack,
    )
}

private fun EntryProviderScope<NavKey>.homeEntry(postsRepository: PostsRepository, isExpandedScreen: Boolean, openDrawer: () -> Unit) {
    entry<HomeKey> {
        val homeViewModel: HomeViewModel = viewModel(
            factory = HomeViewModel.provideFactory(
                postsRepository = postsRepository,
                preSelectedPostId = it.postId,
            ),
        )
        HomeRoute(
            homeViewModel = homeViewModel,
            isExpandedScreen = isExpandedScreen,
            openDrawer = openDrawer,
        )
    }
}

private fun EntryProviderScope<NavKey>.interestsEntry(
    interestsRepository: InterestsRepository,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
) {
    entry<InterestsKey> {
        val interestsViewModel: InterestsViewModel = viewModel(
            factory = InterestsViewModel.provideFactory(interestsRepository),
        )
        InterestsRoute(
            interestsViewModel = interestsViewModel,
            isExpandedScreen = isExpandedScreen,
            openDrawer = openDrawer,
        )
    }
}
