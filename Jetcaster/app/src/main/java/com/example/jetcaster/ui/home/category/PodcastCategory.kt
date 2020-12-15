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

package com.example.jetcaster.ui.home.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.ui.home.PreviewEpisodes
import com.example.jetcaster.ui.home.PreviewPodcasts
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.viewModelProviderFactoryOf
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PodcastCategory(
    categoryId: Long,
    modifier: Modifier = Modifier
) {
    /**
     * CategoryEpisodeListViewModel requires the category as part of it's constructor, therefore
     * we need to assist with it's instantiation with a custom factory and custom key.
     */
    val viewModel: PodcastCategoryViewModel = viewModel(
        // We use a custom key, using the category parameter
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { PodcastCategoryViewModel(categoryId) }
    )

    val viewState by viewModel.state.collectAsState()

    /**
     * TODO: reset scroll position when category changes
     */
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            CategoryPodcastRow(
                podcasts = viewState.topPodcasts,
                onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
                modifier = Modifier.fillParentMaxWidth()
            )
        }

        items(viewState.episodes) { item ->
            EpisodeListItem(
                episode = item.episode,
                podcast = item.podcast,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
fun EpisodeListItem(
    episode: Episode,
    podcast: Podcast,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier.clickable { /* TODO */ } then modifier
    ) {
        val (
            divider, episodeTitle, podcastTitle, image, playIcon,
            date, addPlaylist, overflow
        ) = createRefs()

        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)

                width = Dimension.fillToConstraints
            }
        )

        if (podcast.imageUrl != null) {
            // If we have an image Url, we can show it using [CoilImage]
            CoilImage(
                data = podcast.imageUrl,
                fadeIn = true,
                contentScale = ContentScale.Crop,
                loading = { /* TODO do something better here */ },
                modifier = Modifier.preferredSize(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(image) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(parent.top, 16.dp)
                    }
            )
        } else {
            // If we don't have an image url, we need to make sure that the constraint reference
            // still makes senses for our siblings. We add a zero sized spacer in the spacer
            // origin position (top-end) with the same margin
            Spacer(
                Modifier.constrainAs(image) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                }
            )
        }

        Text(
            text = episode.title,
            maxLines = 2,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(episodeTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = Keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)

                width = Dimension.preferredWrapContent
            }
        )

        val titleImageBarrier = createBottomBarrier(podcastTitle, image)

        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(
                text = podcast.title,
                maxLines = 2,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.constrainAs(podcastTitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = Keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(episodeTitle.bottom, 6.dp)

                    width = Dimension.preferredWrapContent
                }
            )
        }

        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(AmbientContentColor.current),
            modifier = Modifier
                .clickable(indication = rememberRipple(bounded = false, radius = 24.dp)) {
                    /* TODO */
                }
                .preferredSize(36.dp)
                .constrainAs(playIcon) {
                    start.linkTo(parent.start, Keyline1)
                    top.linkTo(titleImageBarrier, margin = 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                }
        )

        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(
                text = when {
                    episode.duration != null -> {
                        // If we have the duration, we combine the date/duration via a
                        // formatted string
                        stringResource(
                            R.string.episode_date_duration,
                            MediumDateFormatter.format(episode.published),
                            episode.duration.toMinutes().toInt()
                        )
                    }
                    // Otherwise we just use the date
                    else -> MediumDateFormatter.format(episode.published)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.constrainAs(date) {
                    centerVerticallyTo(playIcon)
                    linkTo(
                        start = playIcon.end,
                        startMargin = 12.dp,
                        end = addPlaylist.start,
                        endMargin = 16.dp,
                        bias = 0f // float this towards the start
                    )
                }
            )

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.constrainAs(addPlaylist) {
                    end.linkTo(overflow.start)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(Icons.Default.PlaylistAdd)
            }

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.constrainAs(overflow) {
                    end.linkTo(parent.end, 8.dp)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(Icons.Default.MoreVert)
            }
        }
    }
}

@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastWithExtraInfo>,
    onTogglePodcastFollowed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = podcasts.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = Keyline1, top = 8.dp, end = Keyline1, bottom = 24.dp)
    ) {
        itemsIndexed(items = podcasts) { index: Int, (podcast, _, isFollowed): PodcastWithExtraInfo ->
            TopPodcastRowItem(
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                isFollowed = isFollowed,
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast.uri) },
                modifier = Modifier.preferredWidth(128.dp)
            )

            if (index < lastIndex) Spacer(Modifier.preferredWidth(24.dp))
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
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (podcastImageUrl != null) {
                CoilImage(
                    data = podcastImageUrl,
                    fadeIn = true,
                    contentScale = ContentScale.Crop,
                    loading = { /* TODO do something better here */ },
                    modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium)
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
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}

@Preview
@Composable
fun PreviewEpisodeListItem() {
    JetcasterTheme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            modifier = Modifier.fillMaxWidth()
        )
    }
}
