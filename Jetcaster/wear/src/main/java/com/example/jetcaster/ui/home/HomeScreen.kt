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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.example.jetcaster.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.paintable.DrawableResPaintable

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onLatestEpisodeClick: () -> Unit,
    onYourPodcastClick: () -> Unit,
    onUpNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    onErrorDialogCancelClick: () -> Unit
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
                    // check if I need a dialog here
                    Dialog(
                        showDialog = true,
                        onDismissRequest = { onErrorDialogCancelClick },
                        scrollState = rememberScalingLazyListState(),
                    ) {
                        Alert(
                            title = {
                                Text(
                                    text = stringResource(R.string.entity_no_featured_podcasts),
                                    color = MaterialTheme.colors.onBackground,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.title3,
                                )
                            },
                        ) {
                            if (viewState
                                .podcastCategoryFilterResult
                                .topPodcasts
                                .isNotEmpty()
                            ) {
                                item {
                                    Chip(
                                        label = viewState.podcastCategoryFilterResult
                                            .topPodcasts[0]
                                            .podcast
                                            .title,
                                        onClick = {
                                            homeViewModel.onTogglePodcastFollowed(
                                                viewState
                                                    .podcastCategoryFilterResult
                                                    .topPodcasts[0]
                                                    .podcast.uri
                                            )
                                        },
                                        colors = ChipDefaults.secondaryChipColors()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
