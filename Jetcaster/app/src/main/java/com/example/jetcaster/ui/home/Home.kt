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
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.RoundedCornerShape
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
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Search
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import androidx.ui.viewmodel.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.Category
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.quantityStringResource
import com.example.jetcaster.util.verticalGradientScrim
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.LocalDate
import java.time.Period

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
                viewState.value.featuredPodcasts,
                viewState.value.refreshing,
                categories = viewState.value.categories,
                selectedCategory = viewState.value.selectedCategory,
                onCategorySelected = viewModel::onCategorySelected,
                modifier = Modifier.fillMaxSize()
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
                .padding(horizontal = Keyline1)
                .size(24.dp)
                .drawBackground(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .gravity(Alignment.CenterEnd)
        )
    }
}

@Composable
fun HomeContent(
    featuredPodcasts: List<Podcast>,
    isRefreshing: Boolean,
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalScroller(
        modifier = modifier
    ) {
        if (featuredPodcasts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.your_podcasts),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = Keyline1, vertical = 16.dp)
            )
            YourPodcasts(
                items = featuredPodcasts,
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(160.dp)
                    .padding(horizontal = Keyline1)
            )

            Spacer(Modifier.height(16.dp))
            Divider()
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }

        Text(
            text = stringResource(R.string.latest_episodes),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(horizontal = Keyline1, vertical = 16.dp)
        )

        if (categories.isNotEmpty()) {
            val selectedIndex = if (selectedCategory != null) {
                categories.indexOfFirst { it == selectedCategory }
            } else 0
            TabRow(
                items = categories,
                selectedIndex = selectedIndex,
                scrollable = true,
                indicatorContainer = { tabPositions ->
                    TabRow.IndicatorContainer(tabPositions, selectedIndex) {
                        TabIndicator(color = MaterialTheme.colors.primary)
                    }
                }
            ) { index, genre ->
                Tab(
                    selected = index == selectedIndex,
                    onSelected = { onCategorySelected(genre) },
                    text = {
                        Text(
                            text = genre.name,
                            style = MaterialTheme.typography.body2
                        )
                    },
                    activeColor = MaterialTheme.colors.primary,
                    inactiveColor = EmphasisAmbient.current.medium.applyEmphasis(contentColor())
                )
            }

            /**
             * TODO, need to think about how this will scroll within the outer VerticalScroller
             */
            CategoryEpisodesList(
                category = selectedCategory ?: categories[0],
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(300.dp) /* TODO, remove this fixed height */
            )
        }
    }
}

@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
    color: Color = contentColor()
) {
    Box(
        modifier.fillMaxWidth()
            .preferredHeight(4.dp)
            .drawBackground(color, RoundedCornerShape(topLeftPercent = 100, topRightPercent = 100))
    )
}

@Composable
fun YourPodcasts(
    items: List<Podcast>,
    modifier: Modifier = Modifier
) {
    LazyRowItems(
        items = items,
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

            podcast.imageUrl?.let { url ->
                CoilImage(
                    data = url,
                    contentScale = ContentScale.Crop,
                    loading = {
                        // TODO we can do something better here
                        Stack(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                Modifier.size(36.dp).gravity(Alignment.Center)
                            )
                        }
                    },
                    modifier = Modifier
                        // We draw a scrim over the image, allowing the text some protection
                        // and contrast. We use a < 1f decay, meaning that the scrim gradient
                        // maintains stronger transparent over a longer distance.
                        .verticalGradientScrim(
                            color = Color.Black.copy(alpha = 0.9f),
                            decay = 0.8f
                        )
                        .constrainAs(artwork) {
                            centerTo(parent)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )
            }

            ProvideEmphasis(EmphasisAmbient.current.high) {
                Text(
                    text = podcast.title,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    modifier = Modifier.constrainAs(name) {
                        start.linkTo(updated.start)
                        end.linkTo(updated.end)
                        bottom.linkTo(updated.top)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            ProvideEmphasis(EmphasisAmbient.current.medium) {
                val mod = Modifier.constrainAs(updated) {
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }

                val lastEpisodeDate = podcast.lastEpisodeDate
                if (lastEpisodeDate != null) {
                    // If we have a last episode date, so display as text. We add some additional
                    // top padding to push the title up slightly
                    Text(
                        text = lastUpdated(lastEpisodeDate.toLocalDate()),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(top = 2.dp).plus(mod)
                    )
                } else {
                    // If we don't have a last episode date, we need to make sure that the
                    // constraint reference still makes senses for our siblings. We add a spacer
                    // in the same position (bottom) with the same margin
                    Spacer(mod)
                }
            }
        }
    }
}

@Composable
private fun lastUpdated(updated: LocalDate): String {
    val period = Period.between(updated, LocalDate.now())
    return when {
        period.months >= 1 -> stringResource(R.string.updated_longer)
        period.days >= 7 -> {
            val weeks = period.days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }
        period.days > 0 -> {
            quantityStringResource(R.plurals.updated_days_ago, period.days, period.days)
        }
        else -> stringResource(R.string.updated_today)
    }
}

@Composable
@Preview
fun PreviewHomeContent() {
    JetcasterTheme {
        HomeContent(
            featuredPodcasts = PreviewPodcasts,
            isRefreshing = false,
            categories = PreviewCategories,
            selectedCategory = null,
            onCategorySelected = {}
        )
    }
}

@Composable
@Preview
fun PreviewPodcastCard() {
    JetcasterTheme {
        PodcastCarouselItem(
            podcast = PreviewPodcasts[0],
            modifier = Modifier.size(128.dp)
        )
    }
}
