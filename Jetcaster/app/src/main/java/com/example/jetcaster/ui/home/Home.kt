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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.jetcaster.R
import com.example.jetcaster.data.Category
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.ui.home.category.PodcastCategoryViewState
import com.example.jetcaster.ui.home.discover.DiscoverViewState
import com.example.jetcaster.ui.home.discover.discoverItems
import com.example.jetcaster.ui.home.library.libraryItems
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.ui.theme.MinContrastOfPrimaryVsSurface
import com.example.jetcaster.util.DynamicThemePrimaryColorsFromImage
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.contrastAgainst
import com.example.jetcaster.util.quantityStringResource
import com.example.jetcaster.util.rememberDominantColorState
import com.example.jetcaster.util.verticalGradientScrim
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlinx.collections.immutable.PersistentList

@Composable
fun Home(
    navigateToPlayer: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize()) {
        Home(
            featuredPodcasts = viewState.featuredPodcasts,
            isRefreshing = viewState.refreshing,
            homeCategories = viewState.homeCategories,
            selectedHomeCategory = viewState.selectedHomeCategory,
            discoverViewState = viewState.discoverViewState,
            podcastCategoryViewState = viewState.podcastCategoryViewState,
            libraryEpisodes = viewState.libraryEpisodes,
            onHomeCategorySelected = viewModel::onHomeCategorySelected,
            onCategorySelected = viewModel::onCategorySelected,
            onPodcastUnfollowed = viewModel::onPodcastUnfollowed,
            navigateToPlayer = navigateToPlayer,
            onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
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
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null
                )
                Icon(
                    painter = painterResource(R.drawable.ic_text_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open search */ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    featuredPodcasts: PersistentList<PodcastWithExtraInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    discoverViewState: DiscoverViewState,
    podcastCategoryViewState: PodcastCategoryViewState,
    libraryEpisodes: List<EpisodeToPodcast>,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
    onHomeCategorySelected: (HomeCategory) -> Unit,
    onCategorySelected: (Category) -> Unit,
    navigateToPlayer: (String) -> Unit,
    onTogglePodcastFollowed: (String) -> Unit,
) {
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
    ) {
        // We dynamically theme this sub-section of the layout to match the selected
        // 'top podcast'

        val surfaceColor = MaterialTheme.colors.surface
        val appBarColor = surfaceColor.copy(alpha = 0.87f)
        val dominantColorState = rememberDominantColorState { color ->
            // We want a color which has sufficient contrast against the surface color
            color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
        }

        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val pagerState = rememberPagerState { featuredPodcasts.size }

            val selectedImageUrl = featuredPodcasts.getOrNull(pagerState.currentPage)
                ?.podcast?.imageUrl

            // When the selected image url changes, call updateColorsFromImageUrl() or reset()
            LaunchedEffect(selectedImageUrl) {
                if (selectedImageUrl != null) {
                    dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
                } else {
                    dominantColorState.reset()
                }
            }

            val scrimColor = MaterialTheme.colors.primary.copy(alpha = 0.38f)

            // Top Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = scrimColor)
            ) {
                // Draw a scrim over the status bar which matches the app bar
                Spacer(
                    Modifier
                        .background(appBarColor)
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                )
                HomeAppBar(
                    backgroundColor = appBarColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Main Content
            HomeContent(
                featuredPodcasts = featuredPodcasts,
                isRefreshing = isRefreshing,
                selectedHomeCategory = selectedHomeCategory,
                homeCategories = homeCategories,
                discoverViewState = discoverViewState,
                podcastCategoryViewState = podcastCategoryViewState,
                libraryEpisodes = libraryEpisodes,
                scrimColor = scrimColor,
                pagerState = pagerState,
                onPodcastUnfollowed = onPodcastUnfollowed,
                onHomeCategorySelected = onHomeCategorySelected,
                onCategorySelected = onCategorySelected,
                navigateToPlayer = navigateToPlayer,
                onTogglePodcastFollowed = onTogglePodcastFollowed
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    featuredPodcasts: PersistentList<PodcastWithExtraInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    discoverViewState: DiscoverViewState,
    podcastCategoryViewState: PodcastCategoryViewState,
    libraryEpisodes: List<EpisodeToPodcast>,
    scrimColor: Color,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
    onHomeCategorySelected: (HomeCategory) -> Unit,
    onCategorySelected: (Category) -> Unit,
    navigateToPlayer: (String) -> Unit,
    onTogglePodcastFollowed: (String) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (featuredPodcasts.isNotEmpty()) {
            item {
                FollowedPodcastItem(
                    items = featuredPodcasts,
                    pagerState = pagerState,
                    onPodcastUnfollowed = onPodcastUnfollowed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalGradientScrim(
                            color = scrimColor,
                            startYPercentage = 1f,
                            endYPercentage = 0f
                        )
                )
            }
        }

        if (isRefreshing) {
            // TODO show a progress indicator or similar
        }

        if (homeCategories.isNotEmpty()) {
            stickyHeader {
                HomeCategoryTabs(
                    categories = homeCategories,
                    selectedCategory = selectedHomeCategory,
                    onCategorySelected = onHomeCategorySelected
                )
            }
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                libraryItems(
                    episodes = libraryEpisodes,
                    navigateToPlayer = navigateToPlayer
                )
            }

            HomeCategory.Discover -> {
                discoverItems(
                    discoverViewState = discoverViewState,
                    podcastCategoryViewState = podcastCategoryViewState,
                    navigateToPlayer = navigateToPlayer,
                    onCategorySelected = onCategorySelected,
                    onTogglePodcastFollowed = onTogglePodcastFollowed
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FollowedPodcastItem(
    items: PersistentList<PodcastWithExtraInfo>,
    pagerState: PagerState,
    onPodcastUnfollowed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FollowedPodcasts(
            items = items,
            pagerState = pagerState,
            onPodcastUnfollowed = onPodcastUnfollowed,
            modifier = Modifier
                .padding(start = Keyline1, top = 16.dp, end = Keyline1)
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(Modifier.height(16.dp))
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
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowedPodcasts(
    items: PersistentList<PodcastWithExtraInfo>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val (podcast, lastEpisodeDate) = items[page]
        FollowedPodcastCarouselItem(
            podcastImageUrl = podcast.imageUrl,
            podcastTitle = podcast.title,
            onUnfollowedClick = { onPodcastUnfollowed(podcast.uri) },
            lastEpisodeDateText = lastEpisodeDate?.let { lastUpdated(it) },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        )
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    podcastTitle: String? = null,
    lastEpisodeDateText: String? = null,
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
                AsyncImage(
                    model = podcastImageUrl,
                    contentDescription = podcastTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )
            }

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        if (lastEpisodeDateText != null) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lastEpisodeDateText,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 8.dp)
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
        Home(
            featuredPodcasts = PreviewPodcastsWithExtraInfo,
            isRefreshing = false,
            homeCategories = HomeCategory.entries,
            selectedHomeCategory = HomeCategory.Discover,
            discoverViewState = DiscoverViewState(
                categories = PreviewCategories,
                selectedCategory = PreviewCategories.first(),
            ),
            podcastCategoryViewState = PodcastCategoryViewState(
                topPodcasts = PreviewPodcastsWithExtraInfo,
                episodes = PreviewEpisodeToPodcasts,
            ),
            libraryEpisodes = emptyList(),
            onCategorySelected = {},
            onPodcastUnfollowed = {},
            navigateToPlayer = {},
            onHomeCategorySelected = {},
            onTogglePodcastFollowed = {}
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
