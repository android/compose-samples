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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.LocalContentColor
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.core.player.model.toPlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.ui.components.MediumDateFormatter
import com.example.jetcaster.ui.components.PlaceholderButton
import com.example.jetcaster.ui.components.PlayIconShape
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun EpisodeScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    episodeViewModel: EpisodeViewModel = hiltViewModel(),
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
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )

    val columnState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = columnState,
        contentPadding = contentPadding,
        modifier = modifier
    ) { contentPadding ->
        when (uiState) {
            is EpisodeScreenState.Loaded -> {
                val title = uiState.episode.episode.title

                EpisodeScreenLoaded(
                    title = title,
                    episode = uiState.episode.toPlayerEpisode(),
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onAddToQueue = onAddToQueue,
                    columnState = columnState,
                    contentPadding = contentPadding
                )
            }

            EpisodeScreenState.Empty -> {
                AlertDialog(
                    visible = true,
                    onDismissRequest = { onDismiss },
                    title = { stringResource(R.string.episode_info_not_available) }
                )
            }

            EpisodeScreenState.Loading -> {
                EpisodeScreenLoading(columnState, contentPadding, modifier)
            }
        }
    }
}

@Composable
fun EpisodeScreenLoaded(
    title: String,
    episode: PlayerEpisode,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    columnState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = columnState,
        contentPadding = contentPadding
    ) {
        item {
            ListHeader {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
        item {
            LoadedButtonsContent(
                episode = episode,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisode = onPlayEpisode,
                onAddToQueue = onAddToQueue
            )
        }
        episodeInfoContent(episode = episode)
    }
}

@Composable
fun LoadedButtonsContent(
    episode: PlayerEpisode,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onAddToQueue: (PlayerEpisode) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val playInteractionSource = remember { MutableInteractionSource() }
    val addToQueueInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        contentAlignment = Alignment.Center
    ) {
        ButtonGroup(Modifier.fillMaxWidth()) {

            FilledIconButton(
                onClick = {
                    onPlayButtonClick()
                    onPlayEpisode(episode)
                },
                modifier = Modifier
                    .weight(weight = 0.7F)
                    .animateWidth(playInteractionSource),
                enabled = enabled,
                interactionSource = playInteractionSource,
                shapes = PlayIconShape()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = stringResource(id = R.string.button_play_content_description)
                )
            }

            FilledIconButton(
                onClick = { onAddToQueue(episode) },
                modifier = Modifier
                    .weight(weight = 0.3F)
                    .animateWidth(addToQueueInteractionSource),
                interactionSource = addToQueueInteractionSource,
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = stringResource(id = R.string.add_to_queue_content_description)
                )
            }
        }
    }
}

@Composable
fun EpisodeScreenLoading(
    columnState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = columnState,
        contentPadding = contentPadding
    ) {
        item {
            ListHeader {
                Text(text = stringResource(R.string.loading))
            }
        }
        item {
            LoadingButtonsContent()
        }
        items(count = 2) {
            PlaceholderButton()
        }
    }
}

@Composable
fun LoadingButtonsContent() {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {

        FilledIconButton(
            onClick = {
            },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = false,
            shapes = PlayIconShape()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.play),
                contentDescription = stringResource(id = R.string.button_play_content_description)
            )
        }

        FilledIconButton(
            onClick = { },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
            enabled = false
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(id = R.string.add_to_queue_content_description)
            )
        }
    }
}

private fun TransformingLazyColumnScope.episodeInfoContent(episode: PlayerEpisode) {
    val author = episode.author
    val duration = episode.duration
    val published = episode.published
    val summary = episode.summary

    if (!author.isNullOrEmpty()) {
        item {
            Text(
                text = author,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
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
                        duration.toMinutes().toInt(),
                    )
                }
                // Otherwise we just use the date
                else -> MediumDateFormatter.format(published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp),
        )
    }
    if (summary != null) {
        val summaryInParagraphs = summary.split("\n+".toRegex()).orEmpty()
        items(summaryInParagraphs) {
            HtmlTextContainer(text = summary) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current,
                    modifier = Modifier.listTextPadding(),
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun EpisodeScreenEmptyPreview() {
    val uiState: EpisodeScreenState = EpisodeScreenState.Empty
    EpisodeScreen(
        uiState = uiState,
        onPlayButtonClick = { },
        onPlayEpisode = { _ -> },
        onAddToQueue = { _ -> },
        onDismiss = {}
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun EpisodeScreenLoadingPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )

    val columnState = rememberTransformingLazyColumnState()
    EpisodeScreenLoading(columnState, contentPadding)
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun EpisodeScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode
) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )

    EpisodeScreenLoaded(
        title = episode.title,
        episode = episode,
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onAddToQueue = { },
        columnState = columnState,
        contentPadding = contentPadding
    )
}
