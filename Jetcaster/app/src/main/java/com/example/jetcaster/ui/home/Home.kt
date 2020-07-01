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
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.graphics.Color
import androidx.ui.layout.ConstraintLayout
import androidx.ui.layout.Dimension
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.size
import androidx.ui.material.Card
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Search
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import androidx.ui.viewmodel.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.ui.theme.JetcasterTheme
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun Home() {
    val viewModel: HomeViewModel = viewModel()

    val viewState = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            HomeAppBar(
                // TODO: change height to 48.dp in landscape
                Modifier.fillMaxWidth()
                    .preferredHeight(56.dp)
            )
        },
        bodyContent = {
            HomeContent(
                viewState.value.podcasts,
                viewState.value.refreshing,
                Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier
) {
    Stack(modifier = modifier) {
        ProvideEmphasis(EmphasisAmbient.current.medium) {
            IconButton(
                onClick = { /* TODO: Open search */ },
                modifier = Modifier.gravity(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(Icons.Filled.Search)
            }
        }

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.gravity(Alignment.Center)
        )

        Box(
            Modifier
                .padding(horizontal = 24.dp)
                .size(24.dp)
                .drawBackground(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .gravity(Alignment.CenterEnd)
        )
    }
}

@Composable
fun HomeContent(
    podcasts: List<Podcast>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    VerticalScroller(
        modifier = modifier
    ) {
        if (podcasts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.your_podcasts),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(16.dp)
            )
            YourPodcasts(
                podcasts = podcasts.take(10),
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(160.dp)
                    .padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(16.dp))
            Divider()
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }
    }
}

@Composable
fun YourPodcasts(
    podcasts: List<Podcast>,
    modifier: Modifier = Modifier
) {
    LazyRowItems(
        items = podcasts,
        modifier = modifier
    ) { podcast ->
        PodcastCarouselItem(
            podcast,
            Modifier.padding(4.dp).preferredWidth(160.dp).fillMaxHeight()
        )
    }
}

@Composable
fun PodcastCarouselItem(
    podcast: Podcast,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        ConstraintLayout(Modifier.fillMaxSize()) {
            val (artwork, name, updated) = createRefs()

            if (podcast.artworkUrl != null) {
                CoilImage(
                    data = podcast.artworkUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.constrainAs(artwork) {
                        centerTo(parent)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                )
            }

            ProvideEmphasis(EmphasisAmbient.current.high) {
                Text(
                    text = podcast.name,
                    style = MaterialTheme.typography.subtitle2,
                    lineHeight = 18.sp,
                    modifier = Modifier.constrainAs(name) {
                        start.linkTo(updated.start)
                        end.linkTo(updated.end)
                        bottom.linkTo(updated.top, margin = 2.dp)
                        width = Dimension.fillToConstraints
                    }
                )

                Text(
                    text = stringResource(R.string.update_caption, podcast.releaseDate),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(updated) {
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                        width = Dimension.fillToConstraints
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewHomeContent() {
    JetcasterTheme {
        HomeContent(
            podcasts = samplePodcasts,
            isRefreshing = false
        )
    }
}

@Composable
@Preview
fun PreviewPodcastCard() {
    JetcasterTheme {
        PodcastCarouselItem(samplePodcasts[0], Modifier.size(128.dp))
    }
}

private val samplePodcasts = listOf(
    Podcast(
        id = 0L,
        name = "Android Developers Backstage",
        artistName = "Android Developers",
        releaseDate = "2020-06-12"
    ),
    Podcast(
        id = 1L,
        name = "Google Developers podcast",
        artistName = "Google Developers",
        releaseDate = "2020-06-15"
    )
)
