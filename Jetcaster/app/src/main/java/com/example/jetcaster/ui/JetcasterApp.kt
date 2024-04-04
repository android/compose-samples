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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.example.jetcaster.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.get
import androidx.window.layout.DisplayFeature
import com.example.jetcaster.R
import com.example.jetcaster.core.data.di.Graph.episodePlayer
import com.example.jetcaster.core.data.di.Graph.episodeStore
import com.example.jetcaster.core.data.di.Graph.podcastStore
import com.example.jetcaster.ui.home.MainScreen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.player.PlayerViewModel
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsViewModel

val LocalSharedElementScopes = compositionLocalOf { SharedElementScopes() }
data class SharedElementScopes(val scope: SharedTransitionScope? = null,
                               val animatedVisibilityScope: AnimatedVisibilityScope? = null)
@Composable
fun JetcasterApp(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    appState: JetcasterAppState = rememberJetcasterAppState()
) {
    if (appState.isOnline) {
        SharedTransitionLayout {
            NavHost(
                navController = appState.navController,
                startDestination = Screen.Home.route,

            ) {
                fun NavGraphBuilder.sharedElementComposable(
                    route: String,
                    arguments: List<NamedNavArgument> = emptyList(),
                    deepLinks: List<NavDeepLink> = emptyList(),
                    enterTransition: (@JvmSuppressWildcards
                    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
                    exitTransition: (@JvmSuppressWildcards
                    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
                    popEnterTransition: (@JvmSuppressWildcards
                    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
                        enterTransition,
                    popExitTransition: (@JvmSuppressWildcards
                    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
                        exitTransition,
                    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
                ) {
                    composable(route, arguments, deepLinks, enterTransition, exitTransition, popEnterTransition, popExitTransition
                    ) {
                        CompositionLocalProvider(
                            LocalSharedElementScopes provides SharedElementScopes(
                                this@SharedTransitionLayout,
                                this@composable
                            )
                        ) {
                            content(it)
                        }
                    }
                }

                sharedElementComposable(Screen.Home.route) { backStackEntry ->
                        MainScreen(
                            windowSizeClass = windowSizeClass,
                            navigateToPlayer = { episode ->
                                appState.navigateToPlayer(episode.uri, backStackEntry)
                            }
                        )
                }
                sharedElementComposable(Screen.Player.route) { backStackEntry ->
                    val playerViewModel: PlayerViewModel = viewModel(
                        factory = PlayerViewModel.provideFactory(
                            owner = backStackEntry,
                            defaultArgs = backStackEntry.arguments
                        )
                    )

                    PlayerScreen(
                        windowSizeClass,
                        displayFeatures,
                        playerViewModel,
                        onBackPress = appState::navigateBack
                    )
                }
                sharedElementComposable(
                    route = Screen.PodcastDetails.route,
                    enterTransition = {
                        fadeIn(
                            animationSpec = tween(
                                300, easing = LinearEasing
                            )
                        ) + slideIntoContainer(
                            animationSpec = tween(300, easing = EaseIn),
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    },
                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(
                                300, easing = LinearEasing
                            )
                        ) + slideOutOfContainer(
                            animationSpec = tween(300, easing = EaseOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    }
                ) { backStackEntry ->
                    val podcastDetailsViewModel: PodcastDetailsViewModel = viewModel(
                        factory = PodcastDetailsViewModel.provideFactory(
                            episodeStore = episodeStore,
                            podcastStore = podcastStore,
                            episodePlayer = episodePlayer,
                            owner = backStackEntry,
                            defaultArgs = backStackEntry.arguments
                        )
                    )
                    PodcastDetailsScreen(
                        viewModel = podcastDetailsViewModel,
                        navigateToPlayer = { episodePlayer ->
                            appState.navigateToPlayer(episodePlayer.uri, backStackEntry)
                        },
                        navigateBack = appState::navigateBack
                    )
                }
            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry_label))
            }
        }
    )
}
