/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.example.jetcaster.theme.WearAppTheme
import com.example.jetcaster.ui.Episode
import com.example.jetcaster.ui.JetcasterNavController.navigateToEpisode
import com.example.jetcaster.ui.JetcasterNavController.navigateToLatestEpisode
import com.example.jetcaster.ui.JetcasterNavController.navigateToPodcastDetails
import com.example.jetcaster.ui.JetcasterNavController.navigateToUpNext
import com.example.jetcaster.ui.JetcasterNavController.navigateToYourPodcast
import com.example.jetcaster.ui.LatestEpisodes
import com.example.jetcaster.ui.PodcastDetails
import com.example.jetcaster.ui.UpNext
import com.example.jetcaster.ui.YourPodcasts
import com.example.jetcaster.ui.episode.EpisodeScreen
import com.example.jetcaster.ui.home.HomeScreen
import com.example.jetcaster.ui.library.LatestEpisodesScreen
import com.example.jetcaster.ui.library.PodcastsScreen
import com.example.jetcaster.ui.library.QueueScreen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.SnackbarViewModel

@Composable
fun WearApp() {

    val navController = rememberSwipeDismissableNavController()
    val navHostState = rememberSwipeDismissableNavHostState()
    val volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory)

    // TODO remove from MediaPlayerScaffold
    val snackBarManager: SnackbarManager = SnackbarManager()
    val snackbarViewModel: SnackbarViewModel = SnackbarViewModel(snackBarManager)

    WearAppTheme {
        MediaPlayerScaffold(
            playerScreen = {
                PlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    },
                )
            },
            libraryScreen = {
                HomeScreen(
                    onLatestEpisodeClick = { navController.navigateToLatestEpisode() },
                    onYourPodcastClick = { navController.navigateToYourPodcast() },
                    onUpNextClick = { navController.navigateToUpNext() }
                )
            },
            categoryEntityScreen = { _, _ -> },
            mediaEntityScreen = {},
            playlistsScreen = {},
            settingsScreen = {},
            navHostState = navHostState,
            snackbarViewModel = snackbarViewModel,
            volumeViewModel = volumeViewModel,
            deepLinkPrefix = "",
            navController = navController,
            additionalNavRoutes = {
                composable(
                    route = LatestEpisodes.navRoute,
                ) {
                    LatestEpisodesScreen(
                        playlistName = stringResource(id = R.string.latest_episodes),
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable(route = YourPodcasts.navRoute) {
                    PodcastsScreen(
                        onPodcastsItemClick = { navController.navigateToPodcastDetails(it.uri) },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable(route = PodcastDetails.navRoute) {
                    PodcastDetailsScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onEpisodeItemClick = { navController.navigateToEpisode(it.uri) },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable(route = UpNext.navRoute) {
                    QueueScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onEpisodeItemClick = { navController.navigateToPlayer() },
                        onDismiss = {
                            navController.popBackStack()
                            navController.navigateToYourPodcast()
                        }
                    )
                }
                composable(route = Episode.navRoute) {
                    EpisodeScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onDismiss = {
                            navController.popBackStack()
                            navController.navigateToYourPodcast()
                        }
                    )
                }
            },

        )
    }
}
