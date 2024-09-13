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

package com.example.jetcaster.ui.latest_episodes

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
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
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

@Composable fun LatestEpisodesScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel()
) {
    val uiState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    LatestEpisodeScreen(
        modifier = modifier,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode
    )
}

@Composable
fun LatestEpisodeScreen(
    uiState: LatestEpisodeScreenState,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
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
            is LatestEpisodeScreenState.Loaded -> {
                LatestEpisodesScreen(
                    episodeList = uiState.episodeList,
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onPlayEpisodes = onPlayEpisodes,
                    modifier = modifier
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
                LatestEpisodesScreenLoading(
                    modifier = modifier
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
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        label = stringResource(id = R.string.button_play_content_description),
        onClick = {
            onPlayButtonClick()
            onPlayEpisodes(episodes)
        },
        modifier = modifier.padding(bottom = 16.dp),
        icon = Icons.Outlined.PlayArrow.asPaintable(),
    )
}

@Composable
fun LatestEpisodesScreen(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.latest_episodes),)
            }
        },
        content = {
            items(count = episodeList.size) { index ->
                MediaContent(
                    episode = episodeList[index],
                    episodeArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue,
                    ),
                    onItemClick = {
                        onPlayButtonClick()
                        onPlayEpisode(episodeList[index])
                    }
                )
            }
        },
        buttonsContent = {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes
            )
        },
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LatestEpisodesScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.latest_episodes),)
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

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    LatestEpisodesScreen(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onPlayEpisodes = { }
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadingPreview() {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    LatestEpisodesScreenLoading()
}
