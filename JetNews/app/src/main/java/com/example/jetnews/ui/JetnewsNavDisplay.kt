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

package com.example.jetnews.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.unveilIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.rememberSceneState
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.home.homeEntry
import com.example.jetnews.ui.interests.interestsEntry
import com.example.jetnews.ui.navigation.NavigationState
import com.example.jetnews.ui.navigation.Navigator
import com.example.jetnews.ui.navigation.PopUpTo
import com.example.jetnews.ui.navigation.rememberListDetailSceneStrategy
import com.example.jetnews.ui.post.PostKey
import com.example.jetnews.ui.post.postEntry

const val PIVOT_FRACTION_OFFSET = .2f

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun JetnewsNavDisplay(
    navigationState: NavigationState,
    navigator: Navigator,
    appContainer: AppContainer,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    isBackEnabled: Boolean,
    openDrawer: () -> Unit,
) {
    // Because the entryProvider is used within a `remember` block during NavEntry decoration,
    // using rememberUpdatedState allows the current value to be accessed even as window
    // configuration changes.
    val currentIsExpandedScreen by rememberUpdatedState(isExpandedScreen)

    val entryProvider = entryProvider {
        homeEntry(
            postsRepository = appContainer.postsRepository,
            isExpandedScreen = { currentIsExpandedScreen },
            openDrawer = openDrawer,
            navigateToPost = { navigator.navigate(PostKey(it), popUpTo = PopUpTo(HomeKey)) },
        )
        postEntry(
            postsRepository = appContainer.postsRepository,
            isExpandedScreen = { currentIsExpandedScreen },
            onBack = navigator::goUp,
        )
        interestsEntry(
            interestsRepository = appContainer.interestsRepository,
            isExpandedScreen = { currentIsExpandedScreen },
            openDrawer = openDrawer,
        )
    }

    val navEntries = navigationState.toDecoratedEntries(entryProvider = entryProvider)

    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>(isExpandedScreen)

    SharedTransitionLayout {
        Surface {
            val sceneState = rememberSceneState(
                entries = navEntries,
                sceneStrategies = listOf(listDetailSceneStrategy),
                sharedTransitionScope = this,
                onBack = onBack,
            )

            val scene = sceneState.currentScene

            val currentInfo = SceneInfo(scene)
            val previousSceneInfos = sceneState.previousScenes.map { SceneInfo(it) }
            val navigationEventState = rememberNavigationEventState(
                currentInfo = currentInfo,
                backInfo = previousSceneInfos,
            )

            NavigationBackHandler(
                state = navigationEventState,
                isBackEnabled = isBackEnabled && scene.previousEntries.isNotEmpty(),
                onBackCompleted = {
                    repeat(navEntries.size - scene.previousEntries.size) { onBack() }
                },
            )

            NavDisplay(
                sceneState = sceneState,
                navigationEventState = navigationEventState,
                predictivePopTransitionSpec = {
                    unveilIn() togetherWith scaleOut(
                        targetScale = .8f,
                        transformOrigin = when (it) {
                            NavigationEvent.EDGE_LEFT -> TransformOrigin(1 - PIVOT_FRACTION_OFFSET, .5f)
                            NavigationEvent.EDGE_RIGHT -> TransformOrigin(PIVOT_FRACTION_OFFSET, .5f)
                            else -> TransformOrigin.Center
                        },
                    )
                },
            )
        }
    }
}
