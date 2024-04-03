/*
 * Copyright 2020 The Android Open Source Project
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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.example.jetcaster.ui.home.category

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.core.data.model.EpisodeInfo
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.data.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.data.model.PodcastInfo
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.home.PreviewEpisodes
import com.example.jetcaster.ui.home.PreviewPodcasts
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.fullWidthItem

context(SharedTransitionScope, AnimatedVisibilityScope)
fun LazyListScope.podcastCategory(
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    item {
        CategoryPodcasts(
            topPodcasts = podcastCategoryFilterResult.topPodcasts,
            navigateToPodcastDetails = navigateToPodcastDetails,
            onTogglePodcastFollowed = onTogglePodcastFollowed
        )
    }

    val episodes = podcastCategoryFilterResult.episodes
    items(episodes, key = { it.episode.uri }) { item ->
        EpisodeListItem(
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}

fun LazyGridScope.podcastCategory(
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    fullWidthItem {
        CategoryPodcasts(
            topPodcasts = podcastCategoryFilterResult.topPodcasts,
            navigateToPodcastDetails = navigateToPodcastDetails,
            onTogglePodcastFollowed = onTogglePodcastFollowed
        )
    }

    val episodes = podcastCategoryFilterResult.episodes
    items(episodes, key = { it.episode.uri }) { item ->
        EpisodeListItem(
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastInfo>,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit
) {
    CategoryPodcastRow(
        podcasts = topPodcasts,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        navigateToPodcastDetails = navigateToPodcastDetails,
        modifier = Modifier.fillMaxWidth()
    )
}

context(SharedTransitionScope, AnimatedVisibilityScope)
@Composable
fun EpisodeListItem(
    episode: Episode,
    podcast: Podcast,
    onClick: (String) -> Unit,
    onQueuePodcast: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.clickable { onClick(episode.uri) }) {
        if (showDivider) {
            HorizontalDivider(
                Modifier.fillMaxWidth()
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = episode.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )

                Text(
                    text = podcast.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            // If we have an image Url, we can show it using Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(podcast.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .requiredSize(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .sharedElement(
                        rememberSharedContentState(key = "player-image-${podcast.uri}-${episode.uri}"),
                        animatedVisibilityScope = this@AnimatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(2000)
                        },
                        clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)
                    ),
            )

        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                imageVector = Icons.Rounded.PlayCircleFilled,
                contentDescription = stringResource(R.string.cd_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 24.dp)
                    ) { /* TODO */ }
                    .size(48.dp)
                    .padding(6.dp)
                    .semantics { role = Role.Button }

            )

            val duration = episode.duration
            Text(
                text = when {
                    duration != null -> {
                        // If we have the duration, we combine the date/duration via a
                        // formatted string
                        stringResource(
                            R.string.episode_date_duration,
                            MediumDateFormatter.format(episode.published),
                            duration.toMinutes().toInt()
                        )
                    }
                    // Otherwise we just use the date
                    else -> MediumDateFormatter.format(episode.published)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    onQueuePodcast(
                        EpisodeToPodcast().apply {
                            this.episode = episode
                            this._podcasts = listOf(podcast)
                        }
                    )
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = stringResource(R.string.cd_add),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.cd_more),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    }
}

@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastInfo>,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = podcasts.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = Keyline1, top = 8.dp, end = Keyline1, bottom = 24.dp)
    ) {
        itemsIndexed(
            items = podcasts,
            key = { _, p -> p.uri }
        ) { index, podcast ->
            TopPodcastRowItem(
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                isFollowed = podcast.isSubscribed ?: false,
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast) },
                modifier = Modifier.width(128.dp).clickable {
                    navigateToPodcastDetails(podcast)
                }
            )

            if (index < lastIndex) Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
private fun TopPodcastRowItem(
    podcastTitle: String,
    isFollowed: Boolean,
    modifier: Modifier = Modifier,
    onToggleFollowClicked: () -> Unit,
    podcastImageUrl: String? = null,
) {
    Column(
        modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (podcastImageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(podcastImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )
            }

            ToggleFollowPodcastIconButton(
                onClick = onToggleFollowClicked,
                isFollowed = isFollowed,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Text(
            text = podcastTitle,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEpisodeListItem() {
    JetcasterTheme {
        AnimatedVisibility(visible = true) {
            SharedTransitionLayout {
                    EpisodeListItem(
                        episode = PreviewEpisodes[0],
                        podcast = PreviewPodcasts[0],
                        onClick = { },
                        onQueueEpisode = { },
                        modifier = Modifier.fillMaxWidth()
                    )
            }
    }
}
