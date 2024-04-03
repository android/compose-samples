/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class)

package com.example.jetcaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.example.jetcaster.theme.WearAppTheme
import com.example.jetcaster.ui.JetcasterNavController.navigateToLatestEpisode
import com.example.jetcaster.ui.JetcasterNavController.navigateToUpNext
import com.example.jetcaster.ui.JetcasterNavController.navigateToYourPodcast
import com.example.jetcaster.ui.LatestEpisodes
import com.example.jetcaster.ui.home.HomeScreen
import com.example.jetcaster.ui.home.HomeViewModel
import com.example.jetcaster.ui.library.LatestEpisodeViewModel
import com.example.jetcaster.ui.library.LatestEpisodesScreen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.player.PlayerViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.SnackbarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            installSplashScreen()
            WearApp()
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp() {

    val navController = rememberSwipeDismissableNavController()
    val navHostState = rememberSwipeDismissableNavHostState()
    val volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory)
    val snackBarManager: SnackbarManager = SnackbarManager()
    val snackbarViewModel: SnackbarViewModel = SnackbarViewModel(snackBarManager)

    WearAppTheme {
        MediaPlayerScaffold(
            playerScreen = {
                PlayerScreen(
                    modifier = Modifier.fillMaxSize(),

                    playerScreenViewModel = viewModel(
                        factory = PlayerViewModel.provideFactory(
                            owner = LocalSavedStateRegistryOwner.current
                        )
                    ),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    },
                )
            },
            libraryScreen = {
                HomeScreen(
                    homeViewModel = HomeViewModel(),
                    onLatestEpisodeClick = { navController.navigateToLatestEpisode() },
                    onYourPodcastClick = { navController.navigateToYourPodcast() },
                    onUpNextClick = { navController.navigateToUpNext() },
                    onErrorDialogCancelClick = { navController.popBackStack() }
                )
            },
            categoryEntityScreen = { _, _ -> },
            mediaEntityScreen = {},
            playlistsScreen = {},
            settingsScreen = {},

            navHostState = navHostState,
            snackbarViewModel = snackbarViewModel,
            volumeViewModel = volumeViewModel,
            timeText = {
                TimeText()
            },
            deepLinkPrefix = "",
            navController = navController,
            additionalNavRoutes = {
                composable(
                    route = LatestEpisodes.navRoute,
                ) {
                    val columnState = rememberColumnState()

                    ScreenScaffold(scrollState = columnState) {
                        LatestEpisodesScreen(
                            columnState = columnState,
                            playlistName = stringResource(id = R.string.latest_episodes),
                            latestEpisodeViewModel = LatestEpisodeViewModel(),
                            onShuffleButtonClick = {},
                            onPlayButtonClick = {}
                        )
                    }
                }
            },

        )
    }
}
