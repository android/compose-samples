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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Card
import androidx.tv.material3.CardScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardLayout
import androidx.tv.material3.Text
import androidx.tv.material3.WideCardLayout
import coil.compose.AsyncImage
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
internal fun Catalog(
    podcastList: PodcastList,
    latestEpisodeList: EpisodeList,
    onPodcastSelected: (PodcastWithExtraInfo) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
) {
    TvLazyColumn(
        modifier = modifier,
        contentPadding = JetcasterAppDefaults.overScanMargin.catalog.intoPaddingValues(),
        verticalArrangement =
        Arrangement.spacedBy(JetcasterAppDefaults.gapSettings.catalogSectionGap)
    ) {
        if (header != null) {
            item { header() }
        }
        item {
            PodcastSection(
                podcastList = podcastList,
                onPodcastSelected = onPodcastSelected,
                title = stringResource(R.string.label_podcast)
            )
        }
        item {
            LatestEpisodeSection(
                episodeList = latestEpisodeList,
                onEpisodeSelected = {},
                title = stringResource(R.string.label_latest_episode)
            )
        }
    }
}

@Composable
private fun PodcastSection(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastWithExtraInfo) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Section(
        title = title,
        modifier = modifier
    ) {
        PodcastRow(podcastList = podcastList, onPodcastSelected = onPodcastSelected)
    }
}

@Composable
private fun LatestEpisodeSection(
    episodeList: EpisodeList,
    onEpisodeSelected: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Section(
        modifier = modifier,
        title = title
    ) {
        EpisodeRow(episodeList = episodeList, onEpisodeSelected = onEpisodeSelected)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String? = null,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        if (title != null) {
            Text(
                text = title,
                style = style,
                modifier = Modifier.padding(JetcasterAppDefaults.padding.sectionTitle)
            )
        }
        content()
    }
}

@Composable
private fun PodcastRow(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastWithExtraInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gapSettings.catalogItemGap),
) {
    TvLazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
    ) {
        items(podcastList) {
            PodcastCard(
                podcast = it.podcast,
                onClick = { onPodcastSelected(it) },
                modifier = Modifier.width(JetcasterAppDefaults.cardWidth.medium)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun PodcastCard(
    podcast: Podcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardCardLayout(
        imageCard = {
            Card(
                onClick = onClick,
                interactionSource = it,
                scale = CardScale.None,
            ) {
                AsyncImage(model = podcast.imageUrl, contentDescription = null)
            }
        },
        title = {
            Text(text = podcast.title, modifier = Modifier.padding(top = 12.dp))
        },
        modifier = modifier,
    )
}

@Composable
private fun EpisodeRow(
    episodeList: EpisodeList,
    onEpisodeSelected: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gapSettings.catalogItemGap),
) {
    TvLazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
    ) {
        items(episodeList) {
            EpisodeCard(
                episode = it,
                onClick = { onEpisodeSelected(it) },
                modifier = Modifier.width(JetcasterAppDefaults.cardWidth.small)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeCard(
    episode: EpisodeToPodcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    WideCardLayout(
        imageCard = {
            EpisodeThumbnail(episode = episode, onClick = onClick, modifier = modifier)
        },
        title = {
            EpisodeMetaData(
                episode = episode,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .width(JetcasterAppDefaults.cardWidth.small * 2)
            )
        },
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeThumbnail(
    episode: EpisodeToPodcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        scale = CardScale.None,
        modifier = modifier,
    ) {
        AsyncImage(model = episode.podcast.imageUrl, contentDescription = null)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeMetaData(episode: EpisodeToPodcast, modifier: Modifier = Modifier) {
    val publishedDate = episode.episode.published
    val duration = episode.episode.duration
    Column(modifier = modifier) {
        Text(
            text = episode.episode.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = episode.podcast.title, style = MaterialTheme.typography.bodySmall)
        if (duration != null) {
            Spacer(
                modifier = Modifier.height(JetcasterAppDefaults.gapSettings.catalogItemGap * 0.8f)
            )
            EpisodeDataAndDuration(offsetDateTime = publishedDate, duration = duration)
        }
    }
}
