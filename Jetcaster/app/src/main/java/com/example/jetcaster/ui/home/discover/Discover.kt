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

package com.example.jetcaster.ui.home.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetcaster.data.Category
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.ui.home.PreviewCategories
import com.example.jetcaster.ui.home.PreviewEpisodes
import com.example.jetcaster.ui.home.PreviewPodcastsWithExtraInfo
import com.example.jetcaster.ui.home.category.PodcastCategory
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.Keyline1

@Composable
fun Discover(
    modifier: Modifier = Modifier,
    discoverViewState: DiscoverViewState,
    navigateToPlayer: (String) -> Unit,
    onTogglePodcastFollowed: (String) -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    val selectedCategory = discoverViewState.selectedCategory

    if (discoverViewState.categories.isNotEmpty() && selectedCategory != null) {
        Column(modifier) {
            Spacer(Modifier.height(8.dp))

            PodcastCategoryTabs(
                categories = discoverViewState.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            /**
             * TODO, need to think about how this will scroll within the outer VerticalScroller
             */
            PodcastCategory(
                modifier = Modifier.fillMaxSize(),
                topPodcasts = discoverViewState.selectedCategoryResentPodcasts,
                episodes = discoverViewState.selectedCategoryEpisodes,
                navigateToPlayer = navigateToPlayer,
                onTogglePodcastFollowed = onTogglePodcastFollowed
            )
        }
    }
    // TODO: empty state
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@Composable
private fun PodcastCategoryTabs(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        divider = {}, /* Disable the built-in divider */
        edgePadding = Keyline1,
        indicator = emptyTabIndicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) }
            ) {
                ChoiceChipContent(
                    text = category.name,
                    selected = index == selectedIndex,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = when {
            selected -> MaterialTheme.colors.primary.copy(alpha = 0.08f)
            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        },
        contentColor = when {
            selected -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverPreview() {
    JetcasterTheme {
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
}

@Preview(showBackground = true)
@Composable
fun PodcastCategoryTabsPreview() {
    JetcasterTheme {
        PodcastCategoryTabs(
            categories =  PreviewCategories,
            selectedCategory = PreviewCategories[0],
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChoiceChipContentPreview() {
    JetcasterTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChoiceChipContent(
                text = "ChoiceChip1",
                selected = true
            )
            ChoiceChipContent(
                text = "ChoiceChip2",
                selected = false
            )
        }
    }
}