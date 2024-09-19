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

package com.example.jetcaster.ui.podcasts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.ui.preview.WearPreviewPodcasts
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
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
        onPodcastsItemClick = onPodcastsItemClick,
        onDismiss = onDismiss
    )
}

@ExperimentalHorologistApi
@Composable
fun PodcastsScreen(
    podcastsScreenState: PodcastsScreenState,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val columnState = rememberResponsiveColumnState()
    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        when (podcastsScreenState) {
            is PodcastsScreenState.Loaded -> PodcastScreenLoaded(
                podcastList = podcastsScreenState.podcastList,
                onPodcastsItemClick = onPodcastsItemClick
            )
            PodcastsScreenState.Empty ->
                PodcastScreenEmpty(onDismiss)
            PodcastsScreenState.Loading ->
                PodcastScreenLoading()
        }
    }
}

@Composable
fun PodcastScreenLoaded(
    podcastList: List<PodcastInfo>,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            ResponsiveListHeader(
                contentPadding = ListHeaderDefaults.firstItemPadding()
            ) {
                Text(text = stringResource(id = R.string.podcasts))
            }
        },
        content = {
            items(count = podcastList.size) {
                    index ->
                MediaContent(
                    podcast = podcastList[index],
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

@Composable
fun PodcastScreenEmpty(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        showDialog = true,
        message = stringResource(R.string.podcasts_no_podcasts),
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun PodcastScreenLoading(
    modifier: Modifier = Modifier
) {
    EntityScreen(
        modifier = modifier,
        headerContent = {
            DefaultEntityScreenHeader(
                title = stringResource(R.string.podcasts)
            )
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenLoadedPreview(
    @PreviewParameter(WearPreviewPodcasts::class) podcasts: PodcastInfo
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    PodcastScreenLoaded(
        podcastList = listOf(podcasts),
        onPodcastsItemClick = {}
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenLoadingPreview() {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    PodcastScreenLoading()
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenEmptyPreview() {
    PodcastScreenEmpty(onDismiss = {})
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
