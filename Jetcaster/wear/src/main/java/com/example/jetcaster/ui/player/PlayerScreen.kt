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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import com.example.jetcaster.R
import com.example.jetcaster.ui.components.SettingsButtons
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.audio.ui.volumeRotaryBehavior
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.components.PodcastControlButtons
import com.google.android.horologist.media.ui.components.background.ArtworkColorBackground
import com.google.android.horologist.media.ui.components.controls.SeekButtonIncrement
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

    PlayerScreen(
        playerScreenViewModel = playerScreenViewModel,
        volumeUiState = volumeUiState,
        onVolumeClick = onVolumeClick,
        onUpdateVolume = { newVolume -> volumeViewModel.setVolume(newVolume) },
        modifier = modifier
    )
}

@OptIn(ExperimentalWearFoundationApi::class, ExperimentalWearMaterialApi::class)
@Composable
private fun PlayerScreen(
    playerScreenViewModel: PlayerViewModel,
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    onUpdateVolume: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by playerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        PlayerScreenUiState.Loading -> LoadingMediaDisplay(modifier)
        PlayerScreenUiState.Empty -> {
            PlayerScreen(
                mediaDisplay = {
                    TextMediaDisplay(
                        title = stringResource(R.string.nothing_playing),
                        subtitle = ""
                    )
                },
                controlButtons = {
                    PodcastControlButtons(
                        onPlayButtonClick = playerScreenViewModel::onPlay,
                        onPauseButtonClick = playerScreenViewModel::onPause,
                        playPauseButtonEnabled = false,
                        playing = false,
                        onSeekBackButtonClick = playerScreenViewModel::onRewindBy,
                        seekBackButtonEnabled = false,
                        onSeekForwardButtonClick = playerScreenViewModel::onAdvanceBy,
                        seekForwardButtonEnabled = false
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = volumeUiState,
                        onVolumeClick = onVolumeClick,
                        playerUiState = PlayerUiState(),
                        onPlaybackSpeedChange = playerScreenViewModel::onPlaybackSpeedChange,
                        enabled = false,
                    )
                },
            )
        }

        is PlayerScreenUiState.Ready -> {
            // When screen is ready, episode is always not null, however EpisodePlayerState may
            // return a null episode
            val episode = state.playerState.episodePlayerState.currentEpisode

            PlayerScreen(
                mediaDisplay = {
                    if (episode != null && episode.title.isNotEmpty()) {
                        TextMediaDisplay(
                            title = episode.podcastName,
                            subtitle = episode.title
                        )
                    } else {
                        TextMediaDisplay(
                            title = stringResource(R.string.nothing_playing),
                            subtitle = ""
                        )
                    }
                },

                controlButtons = {
                    PodcastControlButtons(
                        onPlayButtonClick = playerScreenViewModel::onPlay,
                        onPauseButtonClick = playerScreenViewModel::onPause,
                        playPauseButtonEnabled = true,
                        playing = state.playerState.episodePlayerState.isPlaying,
                        onSeekBackButtonClick = playerScreenViewModel::onRewindBy,
                        seekBackButtonEnabled = true,
                        onSeekForwardButtonClick = playerScreenViewModel::onAdvanceBy,
                        seekForwardButtonEnabled = true,
                        seekBackButtonIncrement = SeekButtonIncrement.Ten,
                        seekForwardButtonIncrement = SeekButtonIncrement.Ten,
                        trackPositionUiModel = state.playerState.trackPositionUiModel
                    )
                },
                buttons = {
                    SettingsButtons(
                        volumeUiState = volumeUiState,
                        onVolumeClick = onVolumeClick,
                        playerUiState = state.playerState,
                        onPlaybackSpeedChange = playerScreenViewModel::onPlaybackSpeedChange,
                        enabled = true,
                    )
                },
                modifier = modifier
                    .rotaryScrollable(
                        volumeRotaryBehavior(
                            volumeUiStateProvider = { volumeUiState },
                            onRotaryVolumeInput = { onUpdateVolume },
                        ),
                        focusRequester = rememberActiveFocusRequester(),
                    ),
                background = {
                    ArtworkColorBackground(
                        paintable = episode?.let { CoilPaintable(episode.podcastImageUrl) },
                        defaultColor = MaterialTheme.colors.primary,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            )
        }
    }
}
