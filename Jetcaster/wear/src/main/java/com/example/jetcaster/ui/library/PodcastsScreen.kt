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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.android.horologist.composables.Section
import com.google.android.horologist.composables.SectionedList
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.Title
import com.google.android.horologist.images.coil.CoilPaintable

@Composable
fun PodcastsScreen(
    podcastsViewModel: PodcastsViewModel = hiltViewModel(),
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onErrorDialogCancelClick: () -> Unit,
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
        onDismissRequest = onErrorDialogCancelClick,
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
                        onClick = onErrorDialogCancelClick,
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
    podcastsScreenState: PodcastsScreenState<PodcastInfo>,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    podcastItemArtworkPlaceholder: Painter? = null,
) {

    val podcastContent: @Composable (podcast: PodcastInfo) -> Unit = { podcast ->
        Chip(
            label = podcast.title,
            onClick = { onPodcastsItemClick(podcast) },
            icon = CoilPaintable(podcast.imageUrl, podcastItemArtworkPlaceholder),
            largeIcon = true,
            colors = ChipDefaults.secondaryChipColors(),
        )
    }

    PodcastsScreen(
        podcastsScreenState = podcastsScreenState,
        modifier = modifier,
        content = { podcast ->
            Chip(
                label = podcast.title,
                onClick = { onPodcastsItemClick(podcast) },
                icon = CoilPaintable(podcast.imageUrl, podcastItemArtworkPlaceholder),
                largeIcon = true,
                colors = ChipDefaults.secondaryChipColors(),
            )
        }
    )
}

@ExperimentalHorologistApi
@Composable
fun <T> PodcastsScreen(
    podcastsScreenState: PodcastsScreenState<T>,
    modifier: Modifier = Modifier,
    content: @Composable (podcast: T) -> Unit,
) {
    val columnState = rememberColumnState()
    ScreenScaffold(scrollState = columnState) {
        SectionedList(
            modifier = modifier,
            columnState = columnState,
        ) {
            val sectionState = when (podcastsScreenState) {
                is PodcastsScreenState.Loaded<T> -> {
                    Section.State.Loaded(podcastsScreenState.podcastList)
                }

                PodcastsScreenState.Empty -> Section.State.Failed
                PodcastsScreenState.Loading -> Section.State.Loading
            }

            section(state = sectionState) {
                header {
                    Title(
                        R.string.podcasts,
                        Modifier.padding(bottom = 12.dp),
                    )
                }

                loaded { content(it) }

                loading(count = 4) {
                    Column {
                        PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
                    }
                }
            }
        }
    }
}

@ExperimentalHorologistApi
public sealed class PodcastsScreenState<out T> {

    public object Loading : PodcastsScreenState<Nothing>()

    public data class Loaded<T>(
        val podcastList: List<T>,
    ) : PodcastsScreenState<T>()

    public object Empty : PodcastsScreenState<Nothing>()
}
