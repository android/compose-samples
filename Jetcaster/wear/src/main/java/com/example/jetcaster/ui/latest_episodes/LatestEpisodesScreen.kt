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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.components.MediaContent
import com.example.jetcaster.ui.components.PlaceholderButton
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import com.google.android.horologist.images.base.util.rememberVectorPainter

@Composable fun LatestEpisodesScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel(),
) {
    val uiState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    LatestEpisodeScreen(
        modifier = modifier,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode,
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
            is LatestEpisodeScreenState.Loaded -> {
                LatestEpisodesScreen(
                    episodeList = uiState.episodeList,
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onPlayEpisodes = onPlayEpisodes,
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    modifier = modifier
                )
            }

            is LatestEpisodeScreenState.Empty -> {
                AlertDialog(
                    visible = true,
                    onDismissRequest = onDismiss,
                    title = { stringResource(R.string.podcasts_no_episode_podcasts) },
                )
            }

            is LatestEpisodeScreenState.Loading -> {
                LatestEpisodesScreenLoading(
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun ButtonsContent(
    episodes: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onPlayButtonClick()
            onPlayEpisodes(episodes)
        },
        enabled = enabled,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.play),
                contentDescription = stringResource(id = R.string.button_play_content_description)
            )
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(stringResource(id = R.string.button_play_content_description))
    }
}

@Composable
fun LatestEpisodesScreen(
    episodeList: List<PlayerEpisode>,
    onPlayButtonClick: () -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    scrollState: TransformingLazyColumnState
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding
    ) {
        item {
            LatestEpisodesListHeader()
        }
        item {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
            )
        }
        items(episodeList) { episode ->
            MediaContent(
                episode = episode,
                episodeArtworkPlaceholder = painterResource(id = R.drawable.music),
                onItemClick = {
                    onPlayButtonClick()
                    onPlayEpisode(episode)
                }
            )
        }
    }
}

@Composable
fun LatestEpisodesListHeader(
    modifier: Modifier = Modifier
) {
    ListHeader {
        Text(
            text = stringResource(id = R.string.latest_episodes),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }
}

@Composable
fun LatestEpisodesScreenLoading(
    contentPadding: PaddingValues,
    scrollState: TransformingLazyColumnState,
    modifier: Modifier = Modifier
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding
    ) {
        item {
            LatestEpisodesListHeader()
        }
        item {
            ButtonsContent(
                episodes = emptyList(),
                onPlayButtonClick = { },
                onPlayEpisodes = { },
                enabled = false
            )
        }
        items(count = 2) {
            PlaceholderButton()
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadedPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode,
) {
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )

    val columnState = rememberTransformingLazyColumnState()
    LatestEpisodesScreen(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onPlayEpisodes = { },
        contentPadding = contentPadding,
        scrollState = columnState
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun LatestEpisodeScreenLoadingPreview() {
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )

    val columnState = rememberTransformingLazyColumnState()
    LatestEpisodesScreenLoading(contentPadding, columnState)
}
