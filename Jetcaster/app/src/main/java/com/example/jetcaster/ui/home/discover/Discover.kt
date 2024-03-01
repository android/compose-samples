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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetcaster.data.Category
import com.example.jetcaster.ui.home.category.PodcastCategoryViewState
import com.example.jetcaster.ui.home.category.podcastCategory
import com.example.jetcaster.ui.theme.Keyline1

data class DiscoverViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)

fun LazyListScope.discoverItems(
    discoverViewState: DiscoverViewState,
    podcastCategoryViewState: PodcastCategoryViewState,
    navigateToPlayer: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onTogglePodcastFollowed: (String) -> Unit,
) {
    if (discoverViewState.categories.isEmpty() || discoverViewState.selectedCategory == null) {
        // TODO: empty state
        return
    }

    item {
        Spacer(Modifier.height(8.dp))

        PodcastCategoryTabs(
            categories = discoverViewState.categories,
            selectedCategory = discoverViewState.selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }

    podcastCategory(
        topPodcasts = podcastCategoryViewState.topPodcasts,
        episodes = podcastCategoryViewState.episodes,
        navigateToPlayer = navigateToPlayer,
        onTogglePodcastFollowed = onTogglePodcastFollowed
    )
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
