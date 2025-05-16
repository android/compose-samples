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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.ui.LocalAnimatedVisibilityScope
import com.example.jetcaster.ui.LocalSharedTransitionScope
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.fullWidthItem

fun LazyGridScope.podcastCategory(
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    removeFromQueue: (EpisodeInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    fullWidthItem {
        CategoryPodcasts(
            topPodcasts = podcastCategoryFilterResult.topPodcasts,
            navigateToPodcastDetails = navigateToPodcastDetails,
            onTogglePodcastFollowed = onTogglePodcastFollowed,
        )
    }

    val episodes = podcastCategoryFilterResult.episodes
    items(episodes, key = { it.episode.uri }) { item ->
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No SharedElementScope found")
        val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No SharedElementScope found")
        with(sharedTransitionScope) {
            EpisodeListItem(
                episode = item.episode,
                podcast = item.podcast,
                onClick = navigateToPlayer,
                onQueueEpisode = onQueueEpisode,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                imageModifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = item.episode.title,
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium),
                ),
                removeFromQueue = removeFromQueue,
            )
        }
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastInfo>,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
) {
    CategoryPodcastRow(
        podcasts = topPodcasts,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        navigateToPodcastDetails = navigateToPodcastDetails,
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastInfo>,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalUncontainedCarousel(
        state = rememberCarouselState { podcasts.count() },
        modifier = modifier.padding(start = 8.dp),
        itemWidth = 128.dp,
        itemSpacing = 4.dp,
    ) { i ->
        val podcast = podcasts[i]
        TopPodcastRowItem(
            podcastTitle = podcast.title,
            podcastImageUrl = podcast.imageUrl,
            isFollowed = podcast.isSubscribed ?: false,
            onToggleFollowClicked = { onTogglePodcastFollowed(podcast) },
            modifier = Modifier
                .width(128.dp)
                .clickable {
                    navigateToPodcastDetails(podcast)
                }
                .maskClip(MaterialTheme.shapes.large),
        )
    }
}

@Composable
private fun TopPodcastRowItem(
    podcastTitle: String,
    podcastImageUrl: String,
    isFollowed: Boolean,
    modifier: Modifier = Modifier,
    onToggleFollowClicked: () -> Unit,
) {
    val gradient = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))

    Box(
        modifier
            .fillMaxWidth()
            .height(128.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.large),
    ) {
        PodcastImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium),
            podcastImageUrl = podcastImageUrl,
            contentDescription = podcastTitle,
        )

        ToggleFollowPodcastIconButton(
            onClick = onToggleFollowClicked,
            isFollowed = isFollowed,
            modifier = Modifier.align(Alignment.TopStart),
        )

        Box(modifier = Modifier.matchParentSize().background(gradient))

        Text(
            text = podcastTitle,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp),
        )
    }
}

@Preview
@Composable
fun PreviewEpisodeListItem() {
    JetcasterTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            onClick = { },
            onQueueEpisode = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
