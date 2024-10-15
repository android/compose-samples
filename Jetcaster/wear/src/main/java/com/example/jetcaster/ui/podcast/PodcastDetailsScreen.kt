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

package com.example.jetcaster.ui.podcast

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewPodcastEpisodes
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.MediaContent
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.ImageVectorPaintable.Companion.asPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable fun PodcastDetailsScreen(
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    podcastDetailsViewModel: PodcastDetailsViewModel = hiltViewModel()
) {
    val uiState by podcastDetailsViewModel.uiState.collectAsStateWithLifecycle()

    PodcastDetailsScreen(
        uiState = uiState,
        onEpisodeItemClick = onEpisodeItemClick,
        onPlayEpisode = podcastDetailsViewModel::onPlayEpisodes,
        onDismiss = onDismiss,
        onPlayButtonClick = onPlayButtonClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun PodcastDetailsScreen(
    uiState: PodcastDetailsScreenState,
    onPlayButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onPlayEpisode: (List<PlayerEpisode>) -> Unit,
    onDismiss: () -> Unit
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (uiState) {
            is PodcastDetailsScreenState.Loaded -> {
                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = uiState.podcast.title)
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = uiState.episodeList,
                            onPlayButtonClick = onPlayButtonClick,
                            onPlayEpisode = onPlayEpisode
                        )
                    },
                    content = {
                        items(uiState.episodeList) { episode ->
                            MediaContent(
                                episode = episode,
                                episodeArtworkPlaceholder = rememberVectorPainter(
                                    image = Icons.Default.MusicNote,
                                    tintColor = Color.Blue,
                                ),
                                onEpisodeItemClick
                            )
                        }
                    }
                )
            }

            PodcastDetailsScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = { onDismiss },
                    message = stringResource(R.string.podcasts_no_episode_podcasts)
                )
            }
            PodcastDetailsScreenState.Loading -> {
                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = stringResource(id = R.string.loading))
                        }
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = emptyList(),
                            onPlayButtonClick = { },
                            onPlayEpisode = { }
                        )
                    },
                    content = {
                        items(count = 2) {
                            PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (List<PlayerEpisode>) -> Unit,
) {

    Chip(
        label = stringResource(id = R.string.button_play_content_description),
        onClick = {
            onPlayButtonClick()
            onPlayEpisode(episodes)
        },
        modifier = Modifier.padding(bottom = 16.dp),
        icon = Icons.Outlined.PlayArrow.asPaintable(),
    )
}

@ExperimentalHorologistApi
sealed class PodcastDetailsScreenState {

    data object Loading : PodcastDetailsScreenState()

    data class Loaded(
        val episodeList: List<PlayerEpisode>,
        val podcast: PodcastInfo,
    ) : PodcastDetailsScreenState()

    data object Empty : PodcastDetailsScreenState()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastDetailsScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    PodcastDetailsScreen(
        uiState = PodcastDetailsScreenState.Loaded(
            episodeList = listOf(episode),
            podcast = PreviewPodcastEpisodes.first().podcast
        ),
        onPlayButtonClick = { },
        onEpisodeItemClick = {},
        onPlayEpisode = {},
        onDismiss = {}
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastDetailsScreenLoadingPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    PodcastDetailsScreen(
        uiState = PodcastDetailsScreenState.Loading,
        onPlayButtonClick = { },
        onEpisodeItemClick = {},
        onPlayEpisode = {},
        onDismiss = {}
    )
}
