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

package com.example.jetcaster.ui.queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable fun QueueScreen(
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val uiState by queueViewModel.uiState.collectAsStateWithLifecycle()

    QueueScreen(
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onPlayEpisodes = queueViewModel::onPlayEpisodes,
        modifier = modifier,
        onEpisodeItemClick = onEpisodeItemClick,
        onDeleteQueueEpisodes = queueViewModel::onDeleteQueueEpisodes,
        onDismiss = onDismiss
    )
}

@Composable
fun QueueScreen(
    uiState: QueueScreenState,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
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
            is QueueScreenState.Loaded -> QueueScreenLoaded(
                episodeList = uiState.episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes,
                onEpisodeItemClick = onEpisodeItemClick
            )
            QueueScreenState.Loading -> QueueScreenLoading()
            QueueScreenState.Empty -> QueueScreenEmpty(onDismiss)
        }
    }
}

@Composable
fun QueueScreenLoaded(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(R.string.queue))
            }
        },
        buttonsContent = {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes
            )
        },
        content = {
            items(episodeList) { episode ->
                MediaContent(
                    episode = episode,
                    episodeArtworkPlaceholder = rememberVectorPainter(
                        image = Icons.Default.MusicNote,
                        tintColor = Color.Blue,
                    ),
                    onItemClick = onEpisodeItemClick
                )
            }
        }
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun QueueScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            DefaultEntityScreenHeader(
                title = stringResource(R.string.queue)
            )
        },
        buttonsContent = {
            ButtonsContent(
                episodes = emptyList(),
                onPlayButtonClick = {},
                onPlayEpisodes = {},
                onDeleteQueueEpisodes = { },
                enabled = false
            )
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

@Composable
fun QueueScreenEmpty(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        showDialog = true,
        onDismiss = onDismiss,
        title = stringResource(R.string.display_nothing_in_queue),
        message = stringResource(R.string.no_episodes_from_queue),
        modifier = modifier
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Button(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = {
                onPlayButtonClick()
                onPlayEpisodes(episodes)
            },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = enabled
        )
        Button(
            imageVector = Icons.Outlined.Delete,
            contentDescription =
            stringResource(id = R.string.button_delete_queue_content_description),
            onClick = onDeleteQueueEpisodes,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = enabled
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    QueueScreenLoaded(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisodes = { },
        onDeleteQueueEpisodes = { },
        onEpisodeItemClick = { }
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenLoadingPreview() {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    QueueScreenLoading()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenEmptyPreview() {
    QueueScreenEmpty(onDismiss = {})
}
