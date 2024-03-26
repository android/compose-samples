/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.tv.ui.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.example.jetcaster.core.data.database.model.Category
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.database.model.PodcastWithExtraInfo
import com.example.jetcaster.tv.model.CategoryList
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.JetcasterAppDefaults
import com.example.jetcaster.tv.ui.component.Catalog
import com.example.jetcaster.tv.ui.component.Loading

@Composable
fun DiscoverScreen(
    showPodcastDetails: (Podcast) -> Unit,
    modifier: Modifier = Modifier,
    discoverScreenViewModel: DiscoverScreenViewModel = viewModel()
) {
    val uiState by discoverScreenViewModel.uiState.collectAsState()

    when (val s = uiState) {
        DiscoverScreenUiState.Loading -> {
            Loading(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            )
        }

        is DiscoverScreenUiState.Ready -> {
            CatalogWithCategorySelection(
                categoryList = s.categoryList,
                podcastList = s.podcastList,
                selectedCategory = s.selectedCategory,
                latestEpisodeList = s.latestEpisodeList,
                onPodcastSelected = { showPodcastDetails(it.podcast) },
                onCategorySelected = discoverScreenViewModel::selectCategory,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun CatalogWithCategorySelection(
    categoryList: CategoryList,
    podcastList: PodcastList,
    selectedCategory: Category,
    latestEpisodeList: EpisodeList,
    onPodcastSelected: (PodcastWithExtraInfo) -> Unit,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabRow = remember(categoryList) { FocusRequester() }

    LaunchedEffect(Unit) {
        tabRow.requestFocus()
    }

    Catalog(
        podcastList = podcastList,
        latestEpisodeList = latestEpisodeList,
        onPodcastSelected = onPodcastSelected,
        modifier = modifier,
    ) {
        TabRow(
            selectedTabIndex = categoryList.indexOf(selectedCategory),
            modifier = Modifier.focusRequester(tabRow)
        ) {
            categoryList.forEach {
                Tab(
                    selected = it == selectedCategory,
                    onFocus = {
                        onCategorySelected(it)
                    }
                ) {
                    Text(
                        text = it.name,
                        modifier = Modifier.padding(JetcasterAppDefaults.padding.tab)
                    )
                }
            }
        }
    }
}
