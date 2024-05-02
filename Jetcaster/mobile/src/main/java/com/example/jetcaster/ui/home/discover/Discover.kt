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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R
import com.example.jetcaster.core.model.CategoryInfo
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.home.category.podcastCategory
import com.example.jetcaster.util.fullWidthItem

fun LazyListScope.discoverItems(
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    if (filterableCategoriesModel.isEmpty) {
        // TODO: empty state
        return
    }

    item {
        Spacer(Modifier.height(8.dp))

        PodcastCategoryTabs(
            filterableCategoriesModel = filterableCategoriesModel,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }

    podcastCategory(
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        onQueueEpisode = onQueueEpisode,
    )
}

fun LazyGridScope.discoverItems(
    filterableCategoriesModel: FilterableCategoriesModel,
    podcastCategoryFilterResult: PodcastCategoryFilterResult,
    navigateToPodcastDetails: (PodcastInfo) -> Unit,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onCategorySelected: (CategoryInfo) -> Unit,
    onTogglePodcastFollowed: (PodcastInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
) {
    if (filterableCategoriesModel.isEmpty) {
        // TODO: empty state
        return
    }

    fullWidthItem {
        Spacer(Modifier.height(8.dp))

        PodcastCategoryTabs(
            filterableCategoriesModel = filterableCategoriesModel,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }

    podcastCategory(
        podcastCategoryFilterResult = podcastCategoryFilterResult,
        navigateToPodcastDetails = navigateToPodcastDetails,
        navigateToPlayer = navigateToPlayer,
        onTogglePodcastFollowed = onTogglePodcastFollowed,
        onQueueEpisode = onQueueEpisode,
    )
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@Composable
private fun PodcastCategoryTabs(
    filterableCategoriesModel: FilterableCategoriesModel,
    onCategorySelected: (CategoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = filterableCategoriesModel.categories.indexOf(
        filterableCategoriesModel.selectedCategory
    )
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        divider = {}, /* Disable the built-in divider */
        edgePadding = Keyline1,
        indicator = emptyTabIndicator,
        modifier = modifier
    ) {
        filterableCategoriesModel.categories.forEachIndexed { index, category ->
            ChoiceChipContent(
                text = category.name,
                selected = index == selectedIndex,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // When adding onClick to Surface, it automatically makes this item higher.
    // On the other hand, adding .clickable modifier, doesn't use the same shape as Surface.
    // This way we disable the minimum height requirement
    CompositionLocalProvider(value = LocalMinimumInteractiveComponentEnforcement provides false) {
        Surface(
            color = when {
                selected -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainer
            },
            contentColor = when {
                selected -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier,
            onClick = onClick,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    horizontal = when {
                        selected -> 8.dp
                        else -> 16.dp
                    },
                    vertical = 8.dp
                )
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.cd_selected_category),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
