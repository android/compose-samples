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

package com.example.jetcaster.ui.home

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.ConstraintLayout
import androidx.ui.layout.Dimension
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredSize
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.MoreVert
import androidx.ui.material.icons.filled.PlayCircleFilled
import androidx.ui.material.icons.filled.PlaylistAdd
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import androidx.ui.viewmodel.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.Category
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.viewModelProviderFactoryOf
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CategoryEpisodesList(
    category: Category,
    modifier: Modifier = Modifier
) {
    /**
     * CategoryEpisodeListViewModel requires the category as part of it's constructor, therefore
     * we need to assist with it's instantiation with a custom factory and custom key.
     */
    val viewModel: CategoryEpisodeListViewModel = viewModel(
        // We use a custom key, using the category parameter
        key = "category_list_${category.name}",
        factory = viewModelProviderFactoryOf { CategoryEpisodeListViewModel(category) }
    )

    val state = viewModel.state.collectAsState()

    /**
     * TODO: reset scroll position when category changes
     */
    LazyColumnItems(
        items = state.value.episodes,
        modifier = modifier
    ) { (episode, podcast) ->
        EpisodeListItem(
            episode = episode,
            podcast = podcast,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Suppress("UNUSED_VARIABLE")
@Composable
fun EpisodeListItem(
    episode: Episode,
    podcast: Podcast,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier.clickable { /* TODO */ }.plus(modifier)
    ) {
        val (
            divider, episodeTitle, podcastTitle, summary, image, playIcon,
            date, duration, addPlaylist, overflow
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
                contentScale = ContentScale.Crop,
                loading = { /* TODO do something better here */ },
                modifier = Modifier.preferredSize(48.dp)
                    .clip(MaterialTheme.shapes.small)
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

        ProvideEmphasis(EmphasisAmbient.current.high) {
            Text(
                text = episode.title,
                maxLines = 2,
                lineHeight = 20.sp,
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
        }

        val titleImageBarrier = createBottomBarrier(podcastTitle, image)

        ProvideEmphasis(EmphasisAmbient.current.medium) {
            Text(
                text = podcast.title,
                maxLines = 2,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.constrainAs(podcastTitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = Keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(episodeTitle.bottom, 4.dp)

                    width = Dimension.preferredWrapContent
                }
            )

            episode.summary?.let {
                Text(
                    text = it,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(summary) {
                        start.linkTo(parent.start, Keyline1)
                        end.linkTo(image.end)
                        top.linkTo(titleImageBarrier, 16.dp)

                        width = Dimension.fillToConstraints
                    }
                )
            }
        }

        ProvideEmphasis(EmphasisAmbient.current.high) {
            Image(
                asset = Icons.Default.PlayCircleFilled,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(contentColor()),
                modifier = Modifier.preferredSize(48.dp)
                    .clickable { /* TODO */ }
                    .constrainAs(playIcon) {
                        start.linkTo(parent.start, Keyline1)
                        top.linkTo(summary.bottom, margin = 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
            )
        }

        ProvideEmphasis(EmphasisAmbient.current.medium) {
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
                    start.linkTo(playIcon.end, margin = 16.dp)
                    end.linkTo(addPlaylist.start, margin = 16.dp)
                    centerVerticallyTo(playIcon)

                    width = Dimension.fillToConstraints
                }
            )

            IconButton(
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.PlaylistAdd) },
                modifier = Modifier.constrainAs(addPlaylist) {
                    end.linkTo(overflow.start)
                    centerVerticallyTo(playIcon)
                }
            )

            IconButton(
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.MoreVert) },
                modifier = Modifier.constrainAs(overflow) {
                    end.linkTo(parent.end, 8.dp)
                    centerVerticallyTo(playIcon)
                }
            )
        }
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
