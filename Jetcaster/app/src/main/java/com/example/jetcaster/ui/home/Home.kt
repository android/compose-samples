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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.jetcaster.R
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.ui.JetcasterAppState
import com.example.jetcaster.ui.Screen
import com.example.jetcaster.ui.home.discover.Discover
import com.example.jetcaster.ui.home.discover.DiscoverViewModel
import com.example.jetcaster.ui.home.discover.DiscoverViewState
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.ui.theme.MinContrastOfPrimaryVsSurface
import com.example.jetcaster.util.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home(
    appState: JetcasterAppState,
    backStackEntry: NavBackStackEntry
) {
    val homeViewModel = viewModel<HomeViewModel>()
    val homeViewState by homeViewModel.state.collectAsState()
    val discoverViewViewModel = viewModel<DiscoverViewModel>()
    val discoverViewState by discoverViewViewModel.state.collectAsState()

    val errorState = homeViewModel.errorMessage.collectAsState(initial = null)

    val surfaceColor = MaterialTheme.colors.surface
    val featuredPagerState = rememberPagerState()

    HomeScaffold(
        dominantColorState = rememberDominantColorState { color ->
            color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
        },
        dominantColorSourceUrl = homeViewState.featuredPodcasts.getOrNull(featuredPagerState.currentPage)?.podcast?.imageUrl,
        appBarContent = {
            val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)
            // Draw a scrim over the status bar which matches the app bar
            Spacer(
                Modifier
                    .background(appBarColor)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            HomeAppBar(backgroundColor = appBarColor)
        },
        featuredPodcastsContent = {
            AnimatedVisibility(
                visible = homeViewState.featuredPodcasts.isNotEmpty(),
            ) {
                FollowedPodcasts(
                    modifier = Modifier
                        .padding(start = Keyline1, top = 16.dp, end = Keyline1)
                        .fillMaxWidth()
                        .height(200.dp),
                    items = homeViewState.featuredPodcasts,
                    pagerState = featuredPagerState,
                    onPodcastUnfollowed = homeViewModel::onPodcastUnfollowed
                )

            }
        }
    ) {
        if (homeViewState.homeCategories.isNotEmpty()) {
            HomeCategoryTabs(
                categories = homeViewState.homeCategories,
                selectedCategory = homeViewState.selectedHomeCategory,
                onCategorySelected = homeViewModel::onHomeCategorySelected
            )
        }
        when (homeViewState.selectedHomeCategory) {
            HomeCategory.Library -> {
                // TODO
            }
            HomeCategory.Discover -> {
                Discover(
                    modifier = Modifier
                        .fillMaxWidth(),
                    navigateToPlayer = { episodeUri ->
                        appState.navigateToPlayer(episodeUri, backStackEntry)
                    },
                    discoverViewState = discoverViewState,
                    onCategorySelected = discoverViewViewModel::onCategorySelected,
                    onTogglePodcastFollowed = discoverViewViewModel::onTogglePodcastFollowed
                )
            }
        }
    }
    LaunchedEffect(key1 = errorState) {
        snapshotFlow { errorState.value }
            .collect { error ->
                error?.let {
                    appState.scaffoldState.snackbarHostState.showSnackbar(it)
                }
            }
    }
}

@Composable
private fun HomeScaffold(
    modifier: Modifier = Modifier,
    dominantColorState: DominantColorState,
    dominantColorSourceUrl: String? = null,
    appBarContent: @Composable () -> Unit = {},
    featuredPodcastsContent: @Composable () -> Unit = {},
    mainContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .fillMaxSize()
    ) {
        appBarContent()
        DynamicThemePrimaryColorsFromImage(
            dominantColorState = dominantColorState,
            content = featuredPodcastsContent
        )
        mainContent()
    }
    LaunchedEffect(dominantColorSourceUrl) {
        if (dominantColorSourceUrl != null) {
            dominantColorState.updateColorsFromImageUrl(dominantColorSourceUrl)
        } else {
            dominantColorState.reset()
        }
    }
}

@Composable
private fun HomeAppBar(
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
                    tint = LocalContentColor.current,
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
                        tint = LocalContentColor.current,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        tint = LocalContentColor.current,
                        contentDescription = stringResource(R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )
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
private fun HomeCategoryTabIndicator(
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

@ExperimentalPagerApi // HorizontalPager is experimental
@Composable
private fun FollowedPodcasts(
    items: List<PodcastWithExtraInfo>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (String) -> Unit,
) {
    HorizontalPager(
        count = items.size,
        state = pagerState,
        modifier = Modifier
            .verticalGradientScrim(
                color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .then(modifier)
    ) { page ->
        val (podcast, lastEpisodeDate) = items[page]
        FollowedPodcastCarouselItem(
            podcastImageUrl = podcast.imageUrl,
            podcastTitle = podcast.title,
            lastEpisodeDate = lastEpisodeDate,
            onUnfollowedClick = { onPodcastUnfollowed(podcast.uri) },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    podcastTitle: String? = null,
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
            val podcastImageModifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
            if (podcastImageUrl != null) {
                AsyncImage(
                    model = podcastImageUrl,
                    contentDescription = podcastTitle,
                    contentScale = ContentScale.Crop,
                    modifier = podcastImageModifier
                )
            } else {
                Image(
                    modifier = podcastImageModifier,
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "default pod cast image"
                )
            }

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        if (lastEpisodeDate != null) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lastUpdated(lastEpisodeDate),
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

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun HomeScaffoldPreview() {
    JetcasterTheme {
        val surfaceColor = MaterialTheme.colors.surface

        HomeScaffold(
            dominantColorState = rememberDominantColorState { color ->
                color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
            },
            appBarContent = {
                HomeAppBar(backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.87f))
            },
            featuredPodcastsContent = {
                FollowedPodcasts(
                    modifier = Modifier
                        .padding(start = Keyline1, top = 16.dp, end = Keyline1)
                        .fillMaxWidth()
                        .height(200.dp),
                    items = PreviewPodcastsWithExtraInfo,
                    pagerState = rememberPagerState(),
                    onPodcastUnfollowed = {}
                )
            },
            mainContent = {
                HomeCategoryTabs(
                    categories = HomeCategory.values().asList(),
                    selectedCategory = HomeCategory.Discover,
                    onCategorySelected = {}
                )
                Discover(
                    navigateToPlayer = {},
                    discoverViewState = DiscoverViewState(
                        categories = PreviewCategories,
                        selectedCategory = PreviewCategories.getOrNull(0),
                        selectedCategoryResentPodcasts = PreviewPodcastsWithExtraInfo,
                        selectedCategoryEpisodes = listOf(
                            EpisodeToPodcast().apply {
                                episode = PreviewEpisodes[0]
                                _podcasts = listOf(PreviewPodcastsWithExtraInfo[0].podcast)
                            },
                            EpisodeToPodcast().apply {
                                episode = PreviewEpisodes[1]
                                _podcasts = listOf(PreviewPodcastsWithExtraInfo[0].podcast)
                            }
                        )
                    ),
                    onTogglePodcastFollowed = {},
                    onCategorySelected = {}
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPodcastCard() {
    JetcasterTheme {
        FollowedPodcastCarouselItem(
            modifier = Modifier.size(128.dp),
            onUnfollowedClick = {}
        )
    }
}
