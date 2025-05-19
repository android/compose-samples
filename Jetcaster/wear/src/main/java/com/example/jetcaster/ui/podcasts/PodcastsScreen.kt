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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.PlaceholderState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.placeholder
import androidx.wear.compose.material3.placeholderShimmer
import androidx.wear.compose.material3.rememberPlaceholderState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import coil.compose.AsyncImage
import com.example.jetcaster.R
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.ui.preview.WearPreviewPodcasts
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun PodcastsScreen(
    podcastsViewModel: PodcastsViewModel = hiltViewModel(),
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by podcastsViewModel.uiState.collectAsStateWithLifecycle()
    val placeholderState = rememberPlaceholderState(isVisible = uiState is PodcastsScreenState.Loading)

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
        onDismiss = onDismiss,
        placeholderState = placeholderState,
        modifier = modifier,
    )
}

@Composable
fun PodcastsScreen(
    podcastsScreenState: PodcastsScreenState,
    placeholderState: PlaceholderState,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    ScreenScaffold(
        scrollState = columnState,
        contentPadding = contentPadding,
        modifier = modifier.placeholderShimmer(placeholderState),
    ) {
        when (podcastsScreenState) {
            is PodcastsScreenState.Loaded -> PodcastScreenLoaded(
                podcastList = podcastsScreenState.podcastList,
                onPodcastsItemClick = onPodcastsItemClick,
                columnState = columnState,
                contentPadding = contentPadding,
                placeholderState = placeholderState,
            )
            PodcastsScreenState.Empty ->
                PodcastScreenEmpty(onDismiss)
            PodcastsScreenState.Loading ->
                PodcastScreenLoaded(
                    podcastList = emptyList(),
                    onPodcastsItemClick = { },
                    columnState = columnState,
                    contentPadding = contentPadding,
                    placeholderState = placeholderState,
                )
        }
    }
}

@Composable
fun PodcastScreenLoaded(
    podcastList: List<PodcastInfo>,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    contentPadding: PaddingValues,
    columnState: TransformingLazyColumnState,
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
                    text = stringResource(id = R.string.podcasts),
                    modifier = Modifier.placeholder(placeholderState),
                )
            }
        }
        items(count = podcastList.size) { index ->
            MediaContent(
                podcast = podcastList[index],
                onPodcastsItemClick = onPodcastsItemClick,

            )
        }
    }
}

@Composable
fun PodcastScreenEmpty(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        visible = true,
        title = { Text(stringResource(R.string.podcasts_no_podcasts)) },
        onDismissRequest = { onDismiss },
        modifier = modifier,
    )
}

@Composable
fun MediaContent(
    podcast: PodcastInfo,
    onPodcastsItemClick: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    episodeArtworkPlaceholder: Painter = painterResource(id = R.drawable.music),
) {
    val mediaTitle = podcast.title
    val secondaryLabel = podcast.author

    FilledTonalButton(
        label = {
            Text(
                mediaTitle, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
        },
        onClick = { onPodcastsItemClick(podcast) },
        secondaryLabel = { Text(secondaryLabel, maxLines = 1) },
        icon = {
            AsyncImage(
                model = podcast.imageUrl,
                contentDescription = mediaTitle,
                error = episodeArtworkPlaceholder,
                placeholder = episodeArtworkPlaceholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(
                        ButtonDefaults.LargeIconSize,
                    )
                    .clip(CircleShape),

            )
        },
        modifier = modifier.fillMaxWidth(),

    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenLoadedPreview(@PreviewParameter(WearPreviewPodcasts::class) podcasts: PodcastInfo) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    PodcastScreenLoaded(
        podcastList = listOf(podcasts),
        onPodcastsItemClick = {},
        contentPadding = contentPadding,
        columnState = columnState,
        placeholderState = rememberPlaceholderState(isVisible = false),
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun PodcastScreenEmptyPreview() {
    PodcastScreenEmpty(onDismiss = {})
}
