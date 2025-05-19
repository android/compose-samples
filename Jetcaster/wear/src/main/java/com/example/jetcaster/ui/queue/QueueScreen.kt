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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonShapes
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.MediaContent
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable fun QueueScreen(
    onPlayButtonClick: () -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    queueViewModel: QueueViewModel = hiltViewModel(),
) {
    val uiState by queueViewModel.uiState.collectAsStateWithLifecycle()
    val placeholderState = rememberPlaceholderState(isVisible = uiState is QueueScreenState.Loading)

    QueueScreen(
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        placeholderState = placeholderState,
        onPlayEpisodes = queueViewModel::onPlayEpisodes,
        modifier = modifier,
        onEpisodeItemClick = onEpisodeItemClick,
        onDeleteQueueEpisodes = queueViewModel::onDeleteQueueEpisodes,
        onDismiss = onDismiss,
    )
}

@Composable
fun QueueScreen(
    uiState: QueueScreenState,
    placeholderState: PlaceholderState,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onEpisodeItemClick: (PlayerEpisode) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )

    val columnState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = columnState,
        contentPadding = contentPadding,
        modifier = modifier.placeholderShimmer(placeholderState),
    ) { contentPadding ->
        when (uiState) {
            is QueueScreenState.Loaded -> QueueScreenLoaded(
                episodeList = uiState.episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes,
                onEpisodeItemClick = onEpisodeItemClick,
                columnState = columnState,
                contentPadding = contentPadding,
                placeholderState = placeholderState,
            )
            QueueScreenState.Loading -> QueueScreenLoaded(
                episodeList = emptyList(),
                onPlayButtonClick = { },
                onPlayEpisodes = { },
                onDeleteQueueEpisodes = { },
                onEpisodeItemClick = { },
                columnState = columnState,
                contentPadding = contentPadding,
                placeholderState = placeholderState,
            )
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
    columnState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = columnState,
        contentPadding = contentPadding,
    ) {
        item {
            ListHeader {
                Text(
                    text = stringResource(R.string.queue),
                    modifier = Modifier.placeholder(placeholderState),
                )
            }
        }
        item {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                onDeleteQueueEpisodes = onDeleteQueueEpisodes,
                placeholderState = placeholderState,
            )
        }
        items(episodeList) { episode ->
            MediaContent(
                episode = episode,
                episodeArtworkPlaceholder = painterResource(id = R.drawable.music),
                onItemClick = onEpisodeItemClick,
            )
        }
    }
}

@Composable
fun QueueScreenEmpty(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        visible = true,
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(R.string.display_nothing_in_queue)) },
        text = { Text(stringResource(R.string.no_episodes_from_queue)) },
        modifier = modifier,
    )
}

@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onDeleteQueueEpisodes: () -> Unit,
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        ButtonGroup(Modifier.fillMaxWidth()) {
            FilledIconButton(
                onClick = {
                    onPlayButtonClick()
                    onPlayEpisodes(episodes)
                },
                modifier = Modifier
                    .weight(weight = 0.7F)
                    .animateWidth(interactionSource1)
                    .placeholder(placeholderState = placeholderState),
                enabled = enabled,
                interactionSource = interactionSource1,
                shapes = IconButtonShapes(MaterialTheme.shapes.medium),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = stringResource(id = R.string.button_play_content_description),
                )
            }
            FilledIconButton(
                onClick = onDeleteQueueEpisodes,
                modifier = Modifier
                    .weight(weight = 0.3F)
                    .animateWidth(interactionSource2)
                    .placeholder(placeholderState = placeholderState),
                interactionSource = interactionSource2,
                enabled = enabled,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription =
                    stringResource(id = R.string.button_delete_queue_content_description),
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode,
) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    QueueScreenLoaded(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisodes = { },
        onDeleteQueueEpisodes = { },
        onEpisodeItemClick = { },
        columnState = columnState,
        contentPadding = contentPadding,
        placeholderState = rememberPlaceholderState(isVisible = false),
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun QueueScreenEmptyPreview() {
    QueueScreenEmpty(onDismiss = {})
}
