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

package com.example.jetcaster.tv.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.model.PodcastList
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
internal fun Catalog(
    podcastList: PodcastList,
    latestEpisodeList: EpisodeList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    header: (@Composable () -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = JetcasterAppDefaults.overScanMargin.catalog.intoPaddingValues(),
        verticalArrangement =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.section),
        state = state,
    ) {
        if (header != null) {
            item { header() }
        }
        item {
            PodcastSection(
                podcastList = podcastList,
                onPodcastSelected = onPodcastSelected,
                title = stringResource(R.string.label_podcast)
            )
        }
        item {
            LatestEpisodeSection(
                episodeList = latestEpisodeList,
                onEpisodeSelected = onEpisodeSelected,
                title = stringResource(R.string.label_latest_episode)
            )
        }
    }
}

@Composable
private fun PodcastSection(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Section(
        title = title,
        modifier = modifier
    ) {
        PodcastRow(
            podcastList = podcastList,
            onPodcastSelected = onPodcastSelected,
        )
    }
}

@Composable
private fun LatestEpisodeSection(
    episodeList: EpisodeList,
    onEpisodeSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Section(
        modifier = modifier,
        title = title
    ) {
        EpisodeRow(
            playerEpisodeList = episodeList,
            onSelected = onEpisodeSelected,
        )
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String? = null,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        if (title != null) {
            Text(
                text = title,
                style = style,
                modifier = Modifier.padding(JetcasterAppDefaults.padding.sectionTitle)
            )
        }
        content()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PodcastRow(
    podcastList: PodcastList,
    onPodcastSelected: (PodcastInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = JetcasterAppDefaults.padding.podcastRowContentPadding,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.podcastRow),
) {
    val (focusRequester, firstItem) = remember(podcastList) { FocusRequester.createRefs() }

    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
            .focusRequester(focusRequester)
            .focusProperties {
                exit = {
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
                enter = {
                    if (focusRequester.restoreFocusedChild()) {
                        FocusRequester.Cancel
                    } else {
                        firstItem
                    }
                }
            },
    ) {
        itemsIndexed(podcastList) { index, podcastInfo ->
            val cardModifier = if (index == 0) {
                Modifier.focusRequester(firstItem)
            } else {
                Modifier
            }
            PodcastCard(
                podcastInfo = podcastInfo,
                onClick = { onPodcastSelected(podcastInfo) },
                modifier = cardModifier.width(JetcasterAppDefaults.cardWidth.medium)
            )
        }
    }
}
