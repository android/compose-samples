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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.R
import com.example.jetcaster.data.*
import com.example.jetcaster.ui.home.PreviewEpisodes
import com.example.jetcaster.ui.home.PreviewPodcasts
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.viewModelProviderFactoryOf
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PodcastCategory(
    categoryId: Long,
    navigateToPlayer: (String) -> Unit,
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
    Column(modifier = modifier) {
        CategoryPodcasts(viewState.topPodcasts, viewModel)
        EpisodeList(viewState.episodes, navigateToPlayer)
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastWithExtraInfo>,
    viewModel: PodcastCategoryViewModel
) {
    CategoryPodcastRow(
        podcasts = topPodcasts,
        onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EpisodeList(
    episodes: List<EpisodeToPodcast>,
    navigateToPlayer: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {

        items(episodes, key = { it.episode.uri }) { item ->
            EpisodeListItem(
                episode = item.episode,
                podcast = item.podcast,
                onClick = navigateToPlayer,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        // this renders under the system UI, here we make room so we can scroll
        // out from under the bottom nav bar
        item {
            Spacer(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
    }
}

@Composable
fun EpisodeListItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    podcast: Podcast,
    onClick: (String) -> Unit,
    addToPlayList: (String) -> Unit = {},
    moreMenuClick: (Episode) -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable { onClick(episode.uri) }
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Column(modifier = Modifier.weight(5f)) {
                Text(
                    text = episode.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.subtitle1,
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = podcast.title,
                        maxLines = 2,
                        style = MaterialTheme.typography.subtitle2,
                    )
                }
            }

            val imageModifier = Modifier
                .weight(1f)
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)

            podcast.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = stringResource(R.string.default_podcast_icon_description),
                modifier = imageModifier
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    imageVector = Icons.Rounded.PlayCircleFilled,
                    contentDescription = stringResource(R.string.cd_play),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 24.dp)
                        ) {
                            onClick(episode.uri)
                        }
                        .size(48.dp)
                        .padding(6.dp)
                        .semantics { role = Role.Button }
                )

                Spacer(modifier = Modifier.width(6.dp))

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
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
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { addToPlayList(episode.uri) },
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = stringResource(R.string.cd_add)
                    )
                }

                IconButton(
                    onClick = {
                        moreMenuClick(episode)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.cd_more)
                    )
                }
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
                modifier = Modifier.width(128.dp)
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
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}

@Preview(showBackground = true)
@Composable
fun PreviewEpisodeListItem() {
    JetcasterTheme {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
            EpisodeListItem(
                episode = PreviewEpisodes[0],
                podcast = PreviewPodcasts[0],
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
