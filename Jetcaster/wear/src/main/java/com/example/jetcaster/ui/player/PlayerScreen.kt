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

package com.example.jetcaster.ui.player

/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.rotaryVolumeControlsWithFocus
import com.google.android.horologist.compose.rotaryinput.RotaryDefaults
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.display.LoadingMediaDisplay
import com.google.android.horologist.media.ui.components.display.TextMediaDisplay
import com.google.android.horologist.media.ui.screens.player.PlayerScreen

@Composable
fun PlayerScreen(
    volumeViewModel: VolumeViewModel,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    playerScreenViewModel: PlayerViewModel = hiltViewModel(),
) {
    val volumeUiState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
    val playerUiState by playerScreenViewModel.uiState.collectAsStateWithLifecycle()

    PlayerScreen(
        modifier = modifier,
        playerUiState = playerUiState,
        volumeUiState = volumeUiState,
        onVolumeClick = onVolumeClick,
        onUpdateVolume = { newVolume -> volumeViewModel.setVolume(newVolume) },
    )
}

@Composable
private fun PlayerScreen(
    playerUiState: PlayerUiState?,
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    onUpdateVolume: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlayerScreen(
        mediaDisplay = {
            if (playerUiState != null) {
                playerUiState.episodePlayerState.currentEpisode?.let {
                    TextMediaDisplay(
                        title = it.podcastName,
                        subtitle = it.title
                    )
                }
            } else {
                LoadingMediaDisplay()
            }
        },

        controlButtons = {
            PodcastControlButtons(
                onPlayButtonClick = { /*TODO*/ },
                onPauseButtonClick = { /*TODO*/ },
                playPauseButtonEnabled = true,
                playing = true,
                onSeekBackButtonClick = { /*TODO*/ },
                seekBackButtonEnabled = true,
                onSeekForwardButtonClick = { /*TODO*/ },
                seekForwardButtonEnabled = true
            )
        },
        buttons = {
            SettingsButtons(
                volumeUiState = volumeUiState,
                onVolumeClick = onVolumeClick,
                enabled = true,
            )
        },
        modifier = modifier.rotaryVolumeControlsWithFocus(
            volumeUiStateProvider = { volumeUiState },
            onRotaryVolumeInput = onUpdateVolume,
            localView = LocalView.current,
            isLowRes = RotaryDefaults.isLowResInput(),
        ),
        background = {
            if (playerUiState != null) {
                val artworkUri = playerUiState.episodePlayerState.currentEpisode?.podcastImageUrl
                ArtworkColorBackground(
                    artworkUri = artworkUri,
                    defaultColor = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    )
}
