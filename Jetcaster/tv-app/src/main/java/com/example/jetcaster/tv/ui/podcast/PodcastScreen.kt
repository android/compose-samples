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

package com.example.jetcaster.tv.ui.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.component.Background
import com.example.jetcaster.tv.ui.component.ButtonWithIcon
import com.example.jetcaster.tv.ui.component.EpisodeDataAndDuration
import com.example.jetcaster.tv.ui.component.ErrorState
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.Thumbnail
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
fun PodcastScreen(
    podcastScreenViewModel: PodcastScreenViewModel,
    backToHomeScreen: () -> Unit,
    playEpisode: (Episode) -> Unit,
    showEpisodeDetails: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by podcastScreenViewModel.uiStateFlow.collectAsState()
    when (val s = uiState) {
        PodcastScreenUiState.Loading -> Loading(modifier = modifier)
        PodcastScreenUiState.Error -> ErrorState(backToHome = backToHomeScreen, modifier = modifier)
        is PodcastScreenUiState.Ready -> PodcastDetailsWithBackground(
            podcast = s.podcast,
            episodeList = s.episodeList,
            isSubscribed = s.isSubscribed,
            subscribe = podcastScreenViewModel::subscribe,
            unsubscribe = podcastScreenViewModel::unsubscribe,
            playEpisode = playEpisode,
            showEpisodeDetails = showEpisodeDetails,
        )
    }
}

@Composable
private fun PodcastDetailsWithBackground(
    podcast: Podcast,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (Podcast, Boolean) -> Unit,
    unsubscribe: (Podcast, Boolean) -> Unit,
    playEpisode: (Episode) -> Unit,
    showEpisodeDetails: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Box(modifier = modifier) {
        Background(podcast = podcast)
        PodcastDetails(
            podcast = podcast,
            episodeList = episodeList,
            isSubscribed = isSubscribed,
            subscribe = subscribe,
            unsubscribe = unsubscribe,
            playEpisode = playEpisode,
            focusRequester = focusRequester,
            showEpisodeDetails = showEpisodeDetails,
            modifier = Modifier
                .fillMaxSize()
                .padding(JetcasterAppDefaults.overScanMargin.podcast.intoPaddingValues())
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PodcastDetails(
    podcast: Podcast,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (Podcast, Boolean) -> Unit,
    unsubscribe: (Podcast, Boolean) -> Unit,
    playEpisode: (Episode) -> Unit,
    showEpisodeDetails: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Row(
        modifier = modifier,
        horizontalArrangement =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.twoColumn)
    ) {
        PodcastInfo(
            podcast = podcast,
            isSubscribed = isSubscribed,
            subscribe = subscribe,
            unsubscribe = unsubscribe,
            modifier = Modifier.weight(1f),
        )
        PodcastEpisodeList(
            episodeList = episodeList,
            onEpisodeSelected = { playEpisode(it.episode) },
            onDetailsRequested = showEpisodeDetails,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusRestorer()
                .weight(1f)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PodcastInfo(
    podcast: Podcast,
    isSubscribed: Boolean,
    subscribe: (Podcast, Boolean) -> Unit,
    unsubscribe: (Podcast, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val author = podcast.author
    val description = podcast.description

    Column(modifier = modifier) {
        Thumbnail(podcast = podcast)
        Spacer(modifier = Modifier.height(16.dp))
        if (author != null) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = podcast.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        if (description != null) {
            Text(
                text = description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ToggleSubscriptionButton(
            podcast,
            isSubscribed,
            subscribe,
            unsubscribe,
            modifier = Modifier
                .padding(top = JetcasterAppDefaults.gap.podcastRow)
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ToggleSubscriptionButton(
    podcast: Podcast,
    isSubscribed: Boolean,
    subscribe: (Podcast, Boolean) -> Unit,
    unsubscribe: (Podcast, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (isSubscribed) {
        Icons.Default.Remove
    } else {
        Icons.Default.Add
    }
    val label = if (isSubscribed) {
        stringResource(R.string.label_unsubscribe)
    } else {
        stringResource(R.string.label_subscribe)
    }
    val action = if (isSubscribed) {
        unsubscribe
    } else {
        subscribe
    }
    ButtonWithIcon(
        label = label,
        icon = icon,
        onClick = { action(podcast, isSubscribed) },
        scale = ButtonDefaults.scale(scale = 1f),
        modifier = modifier
    )
}

@Composable
private fun PodcastEpisodeList(
    episodeList: EpisodeList,
    onEpisodeSelected: (EpisodeToPodcast) -> Unit,
    onDetailsRequested: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier
) {
    TvLazyColumn(
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.podcastRow),
        modifier = modifier
    ) {
        items(episodeList) {
            EpisodeListItem(
                episodeToPodcast = it,
                onEpisodeSelected = onEpisodeSelected,
                onInfoClicked = onDetailsRequested
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeListItem(
    episodeToPodcast: EpisodeToPodcast,
    onEpisodeSelected: (EpisodeToPodcast) -> Unit,
    onInfoClicked: (EpisodeToPodcast) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val duration = episodeToPodcast.episode.duration

    ListItem(
        selected = selected,
        onClick = { onInfoClicked(episodeToPodcast) },
        onLongClick = { onEpisodeSelected(episodeToPodcast) },
        supportingContent = {
            if (duration != null) {
                EpisodeDataAndDuration(episodeToPodcast.episode.published, duration)
            }
        },
        modifier = modifier
    ) {
        EpisodeTitle(episode = episodeToPodcast.episode)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeTitle(episode: Episode, modifier: Modifier = Modifier) {
    Text(
        text = episode.title,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}
