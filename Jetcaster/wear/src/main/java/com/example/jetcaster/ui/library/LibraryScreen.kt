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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun LibraryScreen(
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    libraryScreenViewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by libraryScreenViewModel.uiState.collectAsState()

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )

    when (val s = uiState) {
        is LibraryScreenUiState.Loading ->
            LoadingScreen(
                modifier = modifier
            )
        is LibraryScreenUiState.NoSubscribedPodcast ->
            NoSubscribedPodcastScreen(
                columnState = columnState,
                modifier = modifier,
                topPodcasts = s.topPodcasts,
                onTogglePodcastFollowed = libraryScreenViewModel::onTogglePodcastFollowed
            )

        is LibraryScreenUiState.Ready ->
            LibraryScreen(
                columnState = columnState,
                modifier = modifier,
                onLatestEpisodeClick = onLatestEpisodeClick,
                onYourPodcastClick = onYourPodcastClick,
                onUpNextClick = onUpNextClick,
                queue = s.queue
            )
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LoadingScreen(
    modifier: Modifier,
) {
    EntityScreen(
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(R.string.loading))
            }
        },
        modifier = modifier,
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NoSubscribedPodcastScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier,
    topPodcasts: List<PodcastInfo>,
    onTogglePodcastFollowed: (uri: String) -> Unit
) {
    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(
                    modifier = modifier.listTextPadding(),
                    contentColor = MaterialTheme.colors.onSurface
                ) {
                    Text(stringResource(R.string.entity_no_featured_podcasts))
                }
            }
            if (topPodcasts.isNotEmpty()) {
                items(topPodcasts.take(3)) { podcast ->
                    PodcastContent(
                        podcast = podcast,
                        downloadItemArtworkPlaceholder = rememberVectorPainter(
                            image = Icons.Default.MusicNote,
                            tintColor = Color.Blue,
                        ),
                        onClick = {
                            onTogglePodcastFollowed(podcast.uri)
                        },
                    )
                }
            } else {
                item {
                    PlaceholderChip(
                        contentDescription = "",
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastContent(
    podcast: PodcastInfo,
    downloadItemArtworkPlaceholder: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaTitle = podcast.title

    Chip(
        label = mediaTitle,
        onClick = onClick,
        modifier = modifier,
        icon = CoilPaintable(podcast.imageUrl, downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}

@Composable
fun LibraryScreen(
    columnState: ScalingLazyColumnState,
    modifier: Modifier,
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    queue: List<PlayerEpisode>
) {
    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                    Text(stringResource(R.string.home_library))
                }
            }
            item {
                Chip(
                    label = stringResource(R.string.latest_episodes),
                    onClick = onLatestEpisodeClick,
                    icon = DrawableResPaintable(R.drawable.new_releases),
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
            item {
                Chip(
                    label = stringResource(R.string.podcasts),
                    onClick = onYourPodcastClick,
                    icon = DrawableResPaintable(R.drawable.podcast),
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
            item {
                ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                    Text(stringResource(R.string.queue))
                }
            }
            item {
                if (queue.isEmpty()) {
                    QueueEmpty()
                } else {
                    Chip(
                        label = stringResource(R.string.up_next),
                        onClick = onUpNextClick,
                        icon = DrawableResPaintable(R.drawable.up_next),
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueEmpty() {
    Text(
        text = stringResource(id = R.string.add_episode_to_queue),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body2,
    )
}
