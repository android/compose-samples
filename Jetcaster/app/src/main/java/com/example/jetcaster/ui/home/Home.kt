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

@file:OptIn(ExperimentalFoundationApi::class)

package com.example.jetcaster.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import com.example.jetcaster.core.data.model.CategoryInfo
import com.example.jetcaster.core.data.model.EpisodeInfo
import com.example.jetcaster.core.data.model.FilterableCategoriesModel
import com.example.jetcaster.core.data.model.LibraryInfo
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.data.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.data.model.PodcastInfo
import com.example.jetcaster.ui.home.discover.discoverItems
import com.example.jetcaster.ui.home.library.libraryItems
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsViewModel
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.quantityStringResource
import com.example.jetcaster.util.verticalGradientScrim
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Home(
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        isDestinationHistoryAware = false
    )
    BackHandler(enabled = navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        supportingPane = {
            val podcastUri = navigator.currentDestination?.content ?:
                viewState.featuredPodcasts.firstOrNull()?.uri
            AnimatedPane {
                if (podcastUri.isNullOrEmpty()) {
                    // TODO
                    Text(text = "Empty State")
                } else {
                    val podcastDetailsViewModel = PodcastDetailsViewModel(podcastUri = podcastUri)
                    PodcastDetailsScreen(
                        viewModel = podcastDetailsViewModel,
                        navigateToPlayer = navigateToPlayer,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                navigator.navigateBack()
                            }
                        }
                    )
                }
            }
        },
        mainPane = {
            Home(
                featuredPodcasts = viewState.featuredPodcasts,
                isRefreshing = viewState.refreshing,
                homeCategories = viewState.homeCategories,
                selectedHomeCategory = viewState.selectedHomeCategory,
                filterableCategoriesModel = viewState.filterableCategoriesModel,
                podcastCategoryFilterResult = viewState.podcastCategoryFilterResult,
                library = viewState.library,
                onHomeCategorySelected = viewModel::onHomeCategorySelected,
                onCategorySelected = viewModel::onCategorySelected,
                onPodcastUnfollowed = viewModel::onPodcastUnfollowed,
                navigateToPodcastDetails = {
                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.uri)
                },
                navigateToPlayer = navigateToPlayer,
                onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
                onLibraryPodcastSelected = viewModel::onLibraryPodcastSelected,
                onQueueEpisode = viewModel::onQueueEpisode,
                modifier = Modifier.fillMaxSize()
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        actions = {
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
        },
        modifier = modifier.background(backgroundColor)
    )
}

@Composable
fun Home(
    featuredPodcasts: PersistentList<PodcastInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    onHomeCategorySelected: (HomeCategory) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onLibraryPodcastSelected: (PodcastInfo?) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    // Effect that changes the home category selection when there are no subscribed podcasts
    LaunchedEffect(key1 = featuredPodcasts) {
        if (featuredPodcasts.isEmpty()) {
            onHomeCategorySelected(HomeCategory.Discover)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        ),
        topBar = {
            HomeAppBar(
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        // Main Content
        val scrimColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        val snackBarText = stringResource(id = R.string.episode_added_to_your_queue)
        HomeContent(
            featuredPodcasts = featuredPodcasts,
            isRefreshing = isRefreshing,
            selectedHomeCategory = selectedHomeCategory,
            homeCategories = homeCategories,
            filterableCategoriesModel = filterableCategoriesModel,
            podcastCategoryFilterResult = podcastCategoryFilterResult,
            library = library,
            scrimColor = scrimColor,
            modifier = Modifier.padding(contentPadding),
            onPodcastUnfollowed = onPodcastUnfollowed,
            onHomeCategorySelected = onHomeCategorySelected,
            onCategorySelected = onCategorySelected,
            navigateToPodcastDetails = navigateToPodcastDetails,
            navigateToPlayer = navigateToPlayer,
            onTogglePodcastFollowed = onTogglePodcastFollowed,
            onLibraryPodcastSelected = onLibraryPodcastSelected,
            onQueueEpisode = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(snackBarText)
                }
                onQueueEpisode(it)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    featuredPodcasts: PersistentList<PodcastInfo>,
    isRefreshing: Boolean,
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    library: LibraryInfo,
    scrimColor: Color,
    modifier: Modifier = Modifier,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    onHomeCategorySelected: (HomeCategory) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onLibraryPodcastSelected: (PodcastInfo?) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    val pagerState = rememberPagerState { featuredPodcasts.size }
    LaunchedEffect(pagerState, featuredPodcasts) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                val podcast = featuredPodcasts.getOrNull(pagerState.currentPage)
                onLibraryPodcastSelected(podcast)
            }
    }
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (featuredPodcasts.isNotEmpty()) {
            item {
                FollowedPodcastItem(
                    pagerState = pagerState,
                    items = featuredPodcasts,
                    onPodcastUnfollowed = onPodcastUnfollowed,
                    navigateToPodcastDetails = navigateToPodcastDetails,
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

        if (featuredPodcasts.isNotEmpty() && homeCategories.isNotEmpty()) {
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
                    library = library,
                    navigateToPlayer = navigateToPlayer,
                    onQueueEpisode = onQueueEpisode
                )
            }

            HomeCategory.Discover -> {
                discoverItems(
                    filterableCategoriesModel = filterableCategoriesModel,
                    podcastCategoryFilterResult = podcastCategoryFilterResult,
                    navigateToPodcastDetails = navigateToPodcastDetails,
                    navigateToPlayer = navigateToPlayer,
                    onCategorySelected = onCategorySelected,
                    onTogglePodcastFollowed = onTogglePodcastFollowed,
                    onQueueEpisode = onQueueEpisode
                )
            }
        }
    }
}

@Composable
private fun FollowedPodcastItem(
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        FollowedPodcasts(
            pagerState = pagerState,
            items = items,
            onPodcastUnfollowed = onPodcastUnfollowed,
            navigateToPodcastDetails = navigateToPodcastDetails,
            modifier = Modifier.fillMaxWidth()
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
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

private val FEATURED_PODCAST_IMAGE_WIDTH_DP = 160.dp
private val FEATURED_PODCAST_IMAGE_HEIGHT_DP = 180.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowedPodcasts(
    pagerState: PagerState,
    items: PersistentList<PodcastInfo>,
    onPodcastUnfollowed: (PodcastInfo) -> Unit,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(modifier) {
        val horizontalPadding = (this.maxWidth - FEATURED_PODCAST_IMAGE_WIDTH_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_PODCAST_IMAGE_WIDTH_DP)
        ) { page ->
            val podcast = items[page]
            FollowedPodcastCarouselItem(
                podcastImageUrl = podcast.imageUrl,
                podcastTitle = podcast.title,
                onUnfollowedClick = { onPodcastUnfollowed(podcast) },
                lastEpisodeDateText = podcast.lastEpisodeDate?.let { lastUpdated(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToPodcastDetails(podcast)
                    }
            )
        }
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
    Column(modifier) {
        Box(
            Modifier
                .height(FEATURED_PODCAST_IMAGE_HEIGHT_DP)
                .width(FEATURED_PODCAST_IMAGE_WIDTH_DP)
                .align(Alignment.CenterHorizontally)
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
            Text(
                text = lastEpisodeDateText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
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
            featuredPodcasts = PreviewPodcasts.toPersistentList(),
            isRefreshing = false,
            homeCategories = HomeCategory.entries,
            selectedHomeCategory = HomeCategory.Discover,
            filterableCategoriesModel = FilterableCategoriesModel(
                categories = PreviewCategories,
                selectedCategory = PreviewCategories.firstOrNull()
            ),
            podcastCategoryFilterResult = PodcastCategoryFilterResult(
                topPodcasts = PreviewPodcasts,
                episodes = PreviewPodcastCategoryEpisodes
            ),
            library = LibraryInfo(),
            onCategorySelected = {},
            onPodcastUnfollowed = {},
            navigateToPodcastDetails = {},
            navigateToPlayer = {},
            onHomeCategorySelected = {},
            onTogglePodcastFollowed = {},
            onLibraryPodcastSelected = {},
            onQueueEpisode = {}
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
