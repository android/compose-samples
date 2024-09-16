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

package com.example.jetcaster.ui.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.ui.components.MediumDateFormatter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun EpisodeScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    episodeViewModel: EpisodeViewModel = hiltViewModel()
) {
    val uiState by episodeViewModel.uiState.collectAsStateWithLifecycle()

    EpisodeScreen(
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onPlayEpisode = episodeViewModel::onPlayEpisode,
        onAddToQueue = episodeViewModel::addToQueue,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Composable
fun EpisodeScreen(
    uiState: EpisodeScreenState,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
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
            is EpisodeScreenState.Loaded -> {
                val title = uiState.episode.episode.title

                EntityScreen(
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    buttonsContent = {
                        LoadedButtonsContent(
                            episode = uiState.episode,
                            onPlayButtonClick = onPlayButtonClick,
                            onPlayEpisode = onPlayEpisode,
                            onAddToQueue = onAddToQueue
                        )
                    },
                    content = {
                        episodeInfoContent(episode = uiState.episode)
                    }

                )
            }

            EpisodeScreenState.Empty -> {
                AlertDialog(
                    showDialog = true,
                    onDismiss = { onDismiss },
                    message = stringResource(R.string.episode_info_not_available)
                )
            }
            EpisodeScreenState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun LoadedButtonsContent(
    episode: EpisodeToPodcast,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    enabled: Boolean = true
) {

    Row(
        modifier = Modifier
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
                onPlayEpisode(episode.toPlayerEpisode())
            },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(id = R.string.add_to_queue_content_description),
            onClick = { onAddToQueue(episode.toPlayerEpisode()) },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LoadingScreen() {
    EntityScreen(
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(R.string.loading))
            }
        },
        buttonsContent = {
            LoadingButtonsContent()
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun LoadingButtonsContent() {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {

        Button(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = {},
            enabled = false,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(id = R.string.add_to_queue_content_description),
            onClick = {},
            enabled = false,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}

private fun ScalingLazyListScope.episodeInfoContent(episode: EpisodeToPodcast) {
    val author = episode.episode.author
    val duration = episode.episode.duration
    val published = episode.episode.published
    val summary = episode.episode.summary

    if (!author.isNullOrEmpty()) {
        item {
            Text(
                text = author,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2
            )
        }
    }

    item {
        Text(
            text = when {
                duration != null -> {
                    // If we have the duration, we combine the date/duration via a
                    // formatted string
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(published),
                        duration.toMinutes().toInt()
                    )
                }
                // Otherwise we just use the date
                else -> MediumDateFormatter.format(published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
    if (summary != null) {
        val summaryInParagraphs = summary.split("\n+".toRegex()).orEmpty()
        items(summaryInParagraphs) {
            HtmlTextContainer(text = summary) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    color = LocalContentColor.current,
                    modifier = Modifier.listTextPadding()
                )
            }
        }
    }
}
