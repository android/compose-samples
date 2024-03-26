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

package com.example.jetcaster.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.example.jetcaster.core.data.model.PodcastInfo
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.DrawableResPaintable
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    onErrorDialogCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )
    val viewState by homeViewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(scrollState = columnState, modifier = modifier) {
        ScalingLazyColumn(columnState = columnState) {
            if (viewState.featuredPodcasts.isNotEmpty()) {
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
                    Chip(
                        label = stringResource(R.string.up_next),
                        onClick = onUpNextClick,
                        icon = DrawableResPaintable(R.drawable.up_next),
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            } else {
                item {
                    AlertDialog(
                        message = stringResource(R.string.entity_no_featured_podcasts),
                        showDialog = true,
                        onDismiss = { onErrorDialogCancelClick },
                        content = {

                            if (viewState
                                .podcastCategoryFilterResult
                                .topPodcasts
                                .isNotEmpty()
                            ) {
                                item {
                                    PodcastContent(
                                        podcast = viewState.podcastCategoryFilterResult
                                            .topPodcasts[0],
                                        downloadItemArtworkPlaceholder = rememberVectorPainter(
                                            image = Icons.Default.MusicNote,
                                            tintColor = Color.Blue,
                                        ),
                                        onClick = {
                                            homeViewModel.onTogglePodcastFollowed(
                                                viewState
                                                    .podcastCategoryFilterResult
                                                    .topPodcasts[0]
                                                    .uri
                                            )
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
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun PodcastContent(
    podcast: PodcastInfo,
    downloadItemArtworkPlaceholder: Painter?,
    onClick: () -> Unit
) {
    val mediaTitle = podcast.title

    Chip(
        label = mediaTitle,
        onClick = onClick,
        icon = CoilPaintable(podcast.imageUrl, downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}
