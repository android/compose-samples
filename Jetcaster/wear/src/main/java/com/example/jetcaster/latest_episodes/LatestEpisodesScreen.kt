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

package com.example.jetcaster.latest_episodes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PlayerEpisode
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable fun LatestEpisodesScreen(
    playlistName: String,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel()
) {
    val uiState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    LatestEpisodeScreen(
        modifier = modifier,
        playlistName = playlistName,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode
    )
}

@Composable
fun LatestEpisodeScreen(
    playlistName: String,
    uiState: LatestEpisodeScreenState,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberColumnState()
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (uiState) {
            is LatestEpisodeScreenState.Loaded -> {
                EntityScreen(
                    modifier = modifier,
                    columnState = columnState,
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = playlistName)
                        }
                    },
                    content = {
                        items(count = uiState.episodeList.size) { index ->
                            MediaContent(
                                episode = uiState.episodeList[index],
                                downloadItemArtworkPlaceholder = rememberVectorPainter(
                                    image = Icons.Default.MusicNote,
                                    tintColor = Color.Blue,
                                ),
                                onPlayButtonClick = onPlayButtonClick,
                                onPlayEpisode = onPlayEpisode
                            )
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = uiState.episodeList,
                            onPlayButtonClick = onPlayButtonClick,
                            onPlayEpisodes = onPlayEpisodes
                        )
                    },
                )
            }

            is LatestEpisodeScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = onDismiss,
                    message = stringResource(R.string.podcasts_no_episode_podcasts)
                )
            }

            is LatestEpisodeScreenState.Loading -> {
                EntityScreen(
                    modifier = modifier,
                    columnState = columnState,
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = playlistName)
                        }
                    },
                    content = {
                        items(count = 2) {
                            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = emptyList(),
                            onPlayButtonClick = { },
                            onPlayEpisodes = { },
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun MediaContent(
    episode: PlayerEpisode,
    downloadItemArtworkPlaceholder: Painter?,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit
) {
    val mediaTitle = episode.title

    val secondaryLabel = episode.author

    Chip(
        label = mediaTitle,
        onClick = {
            onPlayButtonClick()
            onPlayEpisode(episode)
        },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(episode.podcastImageUrl, downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
) {
    Chip(
        label = stringResource(id = R.string.button_play_content_description),
        onClick = {
            onPlayButtonClick()
            onPlayEpisodes(episodes)
        },
        modifier = Modifier.padding(bottom = 16.dp),
        icon = Icons.Outlined.PlayArrow.asPaintable(),
    )
}
