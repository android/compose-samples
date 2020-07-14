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
import androidx.compose.getValue
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope.gravity
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.size
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.Scaffold
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.AccountCircle
import androidx.ui.material.icons.filled.Search
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.viewmodel.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.PodcastWithLastEpisodeDate
import com.example.jetcaster.ui.home.discover.Discover
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.Pager
import com.example.jetcaster.util.PagerState
import com.example.jetcaster.util.quantityStringResource
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Composable
fun Home() {
    val viewModel: HomeViewModel = viewModel()

    val viewState by viewModel.state.collectAsState()

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
                featuredPodcasts = viewState.featuredPodcasts,
                isRefreshing = viewState.refreshing,
                homeCategories = viewState.homeCategories,
                selectedHomeCategory = viewState.selectedHomeCategory,
                onCategorySelected = viewModel::onHomeCategorySelected,
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
        ProvideEmphasis(EmphasisAmbient.current.high) {
            Icon(
                asset = vectorResource(R.drawable.ic_text_logo),
                modifier = Modifier.gravity(Alignment.Center)
                    .padding(8.dp)
                    .preferredHeightIn(maxHeight = 24.dp)
            )
        }

        ProvideEmphasis(EmphasisAmbient.current.medium) {
            IconButton(
                onClick = { /* TODO: Open account? */ },
                modifier = Modifier.gravity(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(Icons.Default.AccountCircle)
            }

            IconButton(
                onClick = { /* TODO: Open search */ },
                modifier = Modifier.gravity(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Icon(Icons.Filled.Search)
            }
        }
    }
}

@Composable
fun HomeContent(
    featuredPodcasts: List<PodcastWithLastEpisodeDate>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    modifier: Modifier = Modifier,
    onCategorySelected: (HomeCategory) -> Unit
) {
    Column(modifier = modifier) {
        if (featuredPodcasts.isNotEmpty()) {
            YourPodcasts(
                items = featuredPodcasts,
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(200.dp)
                    .padding(horizontal = Keyline1)
            )

            Spacer(Modifier.height(16.dp))
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }

        if (homeCategories.isNotEmpty()) {
            HomeCategoryTabs(
                categories = homeCategories,
                selectedCategory = selectedHomeCategory,
                onCategorySelected = onCategorySelected
            )
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                // TODO
            }
            HomeCategory.Discover -> {
                Discover(Modifier.fillMaxWidth().weight(1f))
            }
        }
    }
}

@Composable
private fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    TabRow(
        items = categories,
        selectedIndex = selectedIndex,
        indicatorContainer = { tabPositions ->
            TabRow.IndicatorContainer(tabPositions, selectedIndex) {
                HomeCategoryTabIndicator()
            }
        },
        modifier = modifier
    ) { index, category ->
        Tab(
            selected = index == selectedIndex,
            onSelected = { onCategorySelected(category) },
            text = {
                Text(
                    text = stringResource(
                        when (category) {
                            HomeCategory.Library -> R.string.home_library
                            HomeCategory.Discover -> R.string.home_discover
                        }
                    )
                )
            }
        )
    }
}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    Spacer(
        modifier.preferredWidth(112.dp)
            .preferredHeight(4.dp)
            .gravity(Alignment.CenterHorizontally)
            .drawBackground(color, RoundedCornerShape(topLeftPercent = 100, topRightPercent = 100))
    )
}

@Composable
fun YourPodcasts(
    items: List<PodcastWithLastEpisodeDate>,
    modifier: Modifier = Modifier
) {
    val clock = AnimationClockAmbient.current
    val pagerState = remember { PagerState(clock) }

    onCommit(items) {
        pagerState.maxPage = items.size - 1
    }

    Pager(
        state = pagerState,
        modifier = modifier
    ) {
        val (podcast, lastEpisodeDate) = items[page]
        PodcastCarouselItem(
            podcastImageUrl = podcast.imageUrl,
            lastEpisodeDate = lastEpisodeDate,
            modifier = Modifier.padding(4.dp)
                .fillMaxHeight()
                .scalePagerItems(unselectedScale = PodcastCarouselUnselectedScale)
        )
    }
}

private const val PodcastCarouselUnselectedScale = 0.85f

@Composable
fun PodcastCarouselItem(
    podcastImageUrl: String? = null,
    lastEpisodeDate: OffsetDateTime? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (podcastImageUrl != null) {
            CoilImage(
                data = podcastImageUrl,
                contentScale = ContentScale.Crop,
                loading = { /* TODO do something better here */ },
                modifier = Modifier
                    .weight(1f)
                    .gravity(Alignment.CenterHorizontally)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
            )
        }

        if (lastEpisodeDate != null) {
            ProvideEmphasis(EmphasisAmbient.current.medium) {
                Text(
                    text = lastUpdated(lastEpisodeDate),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                        .gravity(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }
        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}

@Composable
@Preview
fun PreviewHomeContent() {
    JetcasterTheme {
        HomeContent(
            featuredPodcasts = PreviewPodcastsWithLastEpisodeDates,
            isRefreshing = false,
            homeCategories = HomeCategory.values().asList(),
            selectedHomeCategory = HomeCategory.Discover,
            onCategorySelected = {}
        )
    }
}

@Composable
@Preview
fun PreviewPodcastCard() {
    JetcasterTheme {
        PodcastCarouselItem(
            modifier = Modifier.size(128.dp)
        )
    }
}
