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

package com.example.jetcaster.podcasts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun PodcastsScreen(
    podcastsViewModel: PodcastsViewModel = hiltViewModel(),
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    val uiState by podcastsViewModel.uiState.collectAsStateWithLifecycle()

    val modifiedState = when (uiState) {
        is PodcastsScreenState.Loaded -> {
            val modifiedPodcast = (uiState as PodcastsScreenState.Loaded).podcastList.map {
                it.takeIf { it.title.isNotEmpty() }
                    ?: it.copy(title = stringResource(id = R.string.no_title))
            }

            PodcastsScreenState.Loaded(modifiedPodcast)
        }

        PodcastsScreenState.Empty,
        PodcastsScreenState.Loading,
        -> uiState
    }

    PodcastsScreen(
        podcastsScreenState = modifiedState,
        onPodcastsItemClick = onPodcastsItemClick
    )

    Dialog(
        showDialog = modifiedState == PodcastsScreenState.Empty,
        onDismissRequest = onDismiss,
        scrollState = rememberScalingLazyListState(),
    ) {
        Alert(
            title = {
                Text(
                    text = stringResource(R.string.podcasts_no_podcasts),
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.title3,
                )
            },
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Button(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(
                            id = R.string
                                .podcasts_failed_dialog_cancel_button_content_description,
                        ),
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(align = Alignment.Center),
                        colors = ButtonDefaults.secondaryButtonColors()
                    )
                }
            }
        }
    }
}

@ExperimentalHorologistApi
@Composable
fun PodcastsScreen(
    podcastsScreenState: PodcastsScreenState,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {

    val columnState = rememberResponsiveColumnState()
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (podcastsScreenState) {
            is PodcastsScreenState.Loaded -> {
                EntityScreen(
                    columnState = columnState,
                    headerContent = {
                        ResponsiveListHeader(
                            contentPadding = ListHeaderDefaults.firstItemPadding()
                        ) {
                            Text(text = stringResource(id = R.string.podcasts))
                        }
                    },
                    content = {
                        items(count = podcastsScreenState.podcastList.size) {
                                index ->
                            MediaContent(
                                podcast = podcastsScreenState.podcastList[index],
                                downloadItemArtworkPlaceholder = rememberVectorPainter(
                                    image = Icons.Default.MusicNote,
                                    tintColor = Color.Blue,
                                ),
                                onPodcastsItemClick = onPodcastsItemClick

                            )
                        }
                    }
                )
            }
            PodcastsScreenState.Empty,
            PodcastsScreenState.Loading -> {
                Column {
                    PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                }
            }
        }
    }
}

@Composable
fun MediaContent(
    podcast: PodcastInfo,
    downloadItemArtworkPlaceholder: Painter?,
    onPodcastsItemClick: (PodcastInfo) -> Unit
) {
    val mediaTitle = podcast.title

    val secondaryLabel = podcast.author

    Chip(
        label = mediaTitle,
        onClick = { onPodcastsItemClick(podcast) },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(podcast.imageUrl, downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}
