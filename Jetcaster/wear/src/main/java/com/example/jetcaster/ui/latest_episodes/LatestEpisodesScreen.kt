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

@Composable fun LatestEpisodesScreen(
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = hiltViewModel(),
) {
    val uiState by latestEpisodeViewModel.uiState.collectAsStateWithLifecycle()
    val placeholderState = rememberPlaceholderState(isVisible = uiState is LatestEpisodeScreenState.Loading)

    LatestEpisodeScreen(
        modifier = modifier,
        uiState = uiState,
        onPlayButtonClick = onPlayButtonClick,
        onDismiss = onDismiss,
        placeholderState = placeholderState,
        onPlayEpisodes = latestEpisodeViewModel::onPlayEpisodes,
        onPlayEpisode = latestEpisodeViewModel::onPlayEpisode,
    )
}

@Composable
fun LatestEpisodeScreen(
    uiState: LatestEpisodeScreenState,
    placeholderState: PlaceholderState,
    onPlayButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    onPlayEpisodes: (List<PlayerEpisode>) -> Unit,
    onPlayEpisode: (PlayerEpisode) -> Unit,
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
            is LatestEpisodeScreenState.Loaded -> {
                LatestEpisodesScreen(
                    episodeList = uiState.episodeList,
                    onPlayButtonClick = onPlayButtonClick,
                    onPlayEpisode = onPlayEpisode,
                    onPlayEpisodes = onPlayEpisodes,
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    placeholderState = placeholderState,
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
                LatestEpisodesScreen(
                    episodeList = emptyList(),
                    onPlayButtonClick = { },
                    onPlayEpisode = { },
                    onPlayEpisodes = {},
                    contentPadding = contentPadding,
                    scrollState = columnState,
                    placeholderState = placeholderState,
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
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
                contentDescription = stringResource(id = R.string.button_play_content_description),
            )
        },
        modifier = modifier.fillMaxWidth().placeholder(placeholderState = placeholderState),
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
    contentPadding: PaddingValues,
    scrollState: TransformingLazyColumnState,
    placeholderState: PlaceholderState,
    modifier: Modifier = Modifier,
) {
    TransformingLazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = contentPadding,
    ) {
        item {
            LatestEpisodesListHeader(placeholderState)
        }
        item {
            ButtonsContent(
                episodes = episodeList,
                onPlayButtonClick = onPlayButtonClick,
                onPlayEpisodes = onPlayEpisodes,
                placeholderState = placeholderState,
            )
        }
        items(episodeList) { episode ->
            MediaContent(
                episode = episode,
                episodeArtworkPlaceholder = painterResource(id = R.drawable.music),
                onItemClick = {
                    onPlayButtonClick()
                    onPlayEpisode(episode)
                },
            )
        }
    }
}

@Composable
fun LatestEpisodesListHeader(placeholderState: PlaceholderState, modifier: Modifier = Modifier) {
    ListHeader(modifier = modifier.placeholder(placeholderState)) {
        Text(
            text = stringResource(id = R.string.latest_episodes),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
        last = ColumnItemType.Button,
    )

    val columnState = rememberTransformingLazyColumnState()
    LatestEpisodesScreen(
        episodeList = listOf(episode),
        onPlayButtonClick = { },
        onPlayEpisode = { },
        onPlayEpisodes = { },
        contentPadding = contentPadding,
        scrollState = columnState,
        placeholderState = rememberPlaceholderState(isVisible = false),
    )
}
