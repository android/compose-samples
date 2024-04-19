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

package com.example.jetcaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import com.example.jetcaster.R
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.model.PlayerEpisode
import com.example.jetcaster.ui.components.LoadingEntityScreen
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable fun QueueScreen(
    onChangeSpeedButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (EpisodeToPodcast) -> Unit,
    onErrorDialogCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    queueViewModel: QueueViewModel = hiltViewModel()
) {
    val uiState by queueViewModel.uiState.collectAsStateWithLifecycle()

    QueueScreen(
        viewState = uiState,
        onChangeSpeedButtonClick = onChangeSpeedButtonClick,
        onEpisodeItemClick = onEpisodeItemClick,
        onErrorDialogCancelClick = onErrorDialogCancelClick,
        onPlayButtonClick = onPlayButtonClick,
        queueViewModel = queueViewModel,
        modifier = modifier,
    )
}

@Composable
fun QueueScreen(
    viewState: QueueScreenState,
    onChangeSpeedButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEpisodeItemClick: (EpisodeToPodcast) -> Unit,
    queueViewModel: QueueViewModel,
    onErrorDialogCancelClick: () -> Unit
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
        when (viewState) {
            is QueueScreenState.Loaded -> {
                EntityScreen(
                    columnState = columnState,
                    headerContent = {
                        DefaultEntityScreenHeader(
                            title = stringResource(R.string.queue)
                        )
                    },
                    buttonsContent = {
                        ButtonsContent(
                            episodes = viewState.episodeList,
                            onChangeSpeedButtonClick = onChangeSpeedButtonClick,
                            onPlayButtonClick = onPlayButtonClick,
                            queueViewModel = queueViewModel
                        )
                    },
                    content = {
                        items(viewState.episodeList) { episode ->
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
            QueueScreenState.Loading -> {
                LoadingEntityScreen(columnState)
            }
            QueueScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = onErrorDialogCancelClick,
                    title = stringResource(R.string.display_nothing_in_queue),
                    message = stringResource(R.string.failed_loading_episodes_from_queue)
                )
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onChangeSpeedButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    queueViewModel: QueueViewModel,
) {

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Button(
            imageVector = ImageVector.vectorResource(R.drawable.speed),
            contentDescription = stringResource(id = R.string.speed_button_content_description),
            onClick = { onChangeSpeedButtonClick() },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = {
                onPlayButtonClick()
                queueViewModel.onPlayEpisode(episodes[0])
            },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}

@Composable
fun MediaContent(
    episode: PlayerEpisode,
    episodeArtworkPlaceholder: Painter?,
    onEpisodeItemClick: (EpisodeToPodcast) -> Unit
) {
    val mediaTitle = episode.title

    val secondaryLabel = episode.author

    Chip(
        label = mediaTitle,
        onClick = { onEpisodeItemClick },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(episode.podcastImageUrl, episodeArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}
