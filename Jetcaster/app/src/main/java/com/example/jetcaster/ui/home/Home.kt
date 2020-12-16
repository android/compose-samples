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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabDefaults.tabIndicatorOffset
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.ui.home.discover.Discover
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.util.DynamicThemePrimaryColorsFromImage
import com.example.jetcaster.util.Pager
import com.example.jetcaster.util.PagerState
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.constrastAgainst
import com.example.jetcaster.util.quantityStringResource
import com.example.jetcaster.util.rememberDominantColorState
import com.example.jetcaster.util.verticalGradientScrim
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Composable
fun Home() {
    val viewModel: HomeViewModel = viewModel()

    val viewState by viewModel.state.collectAsState()

    Surface(Modifier.fillMaxSize()) {
        HomeContent(
            featuredPodcasts = viewState.featuredPodcasts,
            isRefreshing = viewState.refreshing,
            homeCategories = viewState.homeCategories,
            selectedHomeCategory = viewState.selectedHomeCategory,
            onCategorySelected = viewModel::onHomeCategorySelected,
            onPodcastUnfollowed = viewModel::onPodcastUnfollowed,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    imageVector = vectorResource(R.drawable.ic_logo)
                )
                Icon(
                    imageVector = vectorResource(R.drawable.ic_text_logo),
                    modifier = Modifier.padding(start = 4.dp).preferredHeightIn(max = 24.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open search */ }
                ) {
                    Icon(Icons.Filled.Search)
                }
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(Icons.Default.AccountCircle)
                }
            }
        },
        modifier = modifier
    )
}

/**
 * This is the minimum amount of calculated constrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
private const val MinConstastOfPrimaryVsSurface = 3f

@Composable
fun HomeContent(
    featuredPodcasts: List<PodcastWithExtraInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
    onCategorySelected: (HomeCategory) -> Unit
) {
    Column(modifier = modifier) {
        // We dynamically theme this sub-section of the layout to match the selected
        // 'top podcast'

        val surfaceColor = MaterialTheme.colors.surface
        val dominantColorState = rememberDominantColorState { color ->
            // We want a color which has sufficient contrast against the surface color
            color.constrastAgainst(surfaceColor) >= MinConstastOfPrimaryVsSurface
        }

        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val clock = AmbientAnimationClock.current
            val pagerState = remember(clock) { PagerState(clock) }

            val selectedImageUrl = featuredPodcasts.getOrNull(pagerState.currentPage)
                ?.podcast?.imageUrl

            // When the selected image url changes, call updateColorsFromImageUrl() or reset()
            if (selectedImageUrl != null) {
                LaunchedEffect(selectedImageUrl) {
                    dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
                }
            } else {
                dominantColorState.reset()
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalGradientScrim(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
            ) {
                val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)

                // Draw a scrim over the status bar which matches the app bar
                Spacer(Modifier.background(appBarColor).fillMaxWidth().statusBarsHeight())

                HomeAppBar(
                    backgroundColor = appBarColor,
                    modifier = Modifier.fillMaxWidth()
                )

                if (featuredPodcasts.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    FollowedPodcasts(
                        items = featuredPodcasts,
                        pagerState = pagerState,
                        onPodcastUnfollowed = onPodcastUnfollowed,
                        modifier = Modifier
                            .padding(start = Keyline1, top = 16.dp, end = Keyline1)
                            .fillMaxWidth()
                            .preferredHeight(200.dp)
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }
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
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            HomeCategory.Library -> stringResource(R.string.home_library)
                            HomeCategory.Discover -> stringResource(R.string.home_discover)
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }
}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface
) {
    Spacer(
        modifier.padding(horizontal = 24.dp)
            .preferredHeight(4.dp)
            .background(color, RoundedCornerShape(topLeftPercent = 100, topRightPercent = 100))
    )
}

@Composable
fun FollowedPodcasts(
    items: List<PodcastWithExtraInfo>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock) }
    },
    onPodcastUnfollowed: (String) -> Unit,
) {
    pagerState.maxPage = (items.size - 1).coerceAtLeast(0)

    Pager(
        state = pagerState,
        modifier = modifier
    ) {
        val (podcast, lastEpisodeDate) = items[page]
        FollowedPodcastCarouselItem(
            podcastImageUrl = podcast.imageUrl,
            lastEpisodeDate = lastEpisodeDate,
            onUnfollowedClick = { onPodcastUnfollowed(podcast.uri) },
            modifier = Modifier.padding(4.dp).fillMaxHeight()
        )
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    lastEpisodeDate: OffsetDateTime? = null,
    onUnfollowedClick: () -> Unit,
) {
    Column(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
        ) {
            if (podcastImageUrl != null) {
                CoilImage(
                    data = podcastImageUrl,
                    contentScale = ContentScale.Crop,
                    loading = { /* TODO do something better here */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        if (lastEpisodeDate != null) {
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lastUpdated(lastEpisodeDate),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
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
            featuredPodcasts = PreviewPodcastsWithExtraInfo,
            isRefreshing = false,
            homeCategories = HomeCategory.values().asList(),
            selectedHomeCategory = HomeCategory.Discover,
            onCategorySelected = {},
            onPodcastUnfollowed = {}
        )
    }
}

@Composable
@Preview
fun PreviewPodcastCard() {
    JetcasterTheme {
        FollowedPodcastCarouselItem(
            modifier = Modifier.size(128.dp),
            onUnfollowedClick = {}
        )
    }
}
