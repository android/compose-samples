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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.component.BackgroundContainer
import com.example.jetcaster.tv.ui.component.ButtonWithIcon
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDataAndDuration
import com.example.jetcaster.tv.ui.component.ErrorState
import com.example.jetcaster.tv.ui.component.InfoButton
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PlayButton
import com.example.jetcaster.tv.ui.component.Thumbnail
import com.example.jetcaster.tv.ui.component.TwoColumn
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
fun PodcastDetailsScreen(
    backToHomeScreen: () -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    podcastDetailsScreenViewModel: PodcastDetailsScreenViewModel = hiltViewModel(),
) {
    val uiState by podcastDetailsScreenViewModel.uiStateFlow.collectAsState()
    when (val s = uiState) {
        PodcastScreenUiState.Loading -> Loading(modifier = modifier)
        PodcastScreenUiState.Error -> ErrorState(backToHome = backToHomeScreen, modifier = modifier)
        is PodcastScreenUiState.Ready -> PodcastDetailsWithBackground(
            podcastInfo = s.podcastInfo,
            episodeList = s.episodeList,
            isSubscribed = s.isSubscribed,
            subscribe = podcastDetailsScreenViewModel::subscribe,
            unsubscribe = podcastDetailsScreenViewModel::unsubscribe,
            playEpisode = {
                podcastDetailsScreenViewModel.play(it)
                playEpisode(it)
            },
            enqueue = podcastDetailsScreenViewModel::enqueue,
            showEpisodeDetails = showEpisodeDetails,
        )
    }
}

@Composable
private fun PodcastDetailsWithBackground(
    podcastInfo: PodcastInfo,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {

    BackgroundContainer(podcastInfo = podcastInfo, modifier = modifier) {
        PodcastDetails(
            podcastInfo = podcastInfo,
            episodeList = episodeList,
            isSubscribed = isSubscribed,
            subscribe = subscribe,
            unsubscribe = unsubscribe,
            playEpisode = playEpisode,
            focusRequester = focusRequester,
            showEpisodeDetails = showEpisodeDetails,
            enqueue = enqueue,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PodcastDetails(
    podcastInfo: PodcastInfo,
    episodeList: EpisodeList,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    showEpisodeDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    TwoColumn(
        modifier = modifier,
        horizontalArrangement =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.twoColumn),
        first = {
            PodcastInfo(
                podcastInfo = podcastInfo,
                isSubscribed = isSubscribed,
                subscribe = subscribe,
                unsubscribe = unsubscribe,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(
                        JetcasterAppDefaults.overScanMargin.podcast.copy(end = 0.dp)
                            .intoPaddingValues()
                    ),
            )
        },
        second = {
            PodcastEpisodeList(
                episodeList = episodeList,
                playEpisode = { playEpisode(it) },
                showDetails = showEpisodeDetails,
                enqueue = enqueue,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusRestorer()
                    .weight(0.7f)
            )
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun PodcastInfo(
    podcastInfo: PodcastInfo,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Thumbnail(podcastInfo = podcastInfo)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = podcastInfo.author,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = podcastInfo.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = podcastInfo.description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        ToggleSubscriptionButton(
            podcastInfo,
            isSubscribed,
            subscribe,
            unsubscribe,
            modifier = Modifier
                .padding(top = JetcasterAppDefaults.gap.podcastRow)
        )
    }
}

@Composable
private fun ToggleSubscriptionButton(
    podcastInfo: PodcastInfo,
    isSubscribed: Boolean,
    subscribe: (PodcastInfo, Boolean) -> Unit,
    unsubscribe: (PodcastInfo, Boolean) -> Unit,
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
        onClick = { action(podcastInfo, isSubscribed) },
        scale = ButtonDefaults.scale(scale = 1f),
        modifier = modifier
    )
}

@Composable
private fun PodcastEpisodeList(
    episodeList: EpisodeList,
    playEpisode: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.podcastRow),
        modifier = modifier,
        contentPadding = JetcasterAppDefaults.overScanMargin.podcast.intoPaddingValues()
    ) {
        items(episodeList) {
            EpisodeListItem(
                playerEpisode = it,
                onEpisodeSelected = { playEpisode(it) },
                onInfoClicked = { showDetails(it) },
                onEnqueueClicked = { enqueue(it) },
            )
        }
    }
}

@Composable
private fun EpisodeListItem(
    playerEpisode: PlayerEpisode,
    onEpisodeSelected: () -> Unit,
    onInfoClicked: () -> Unit,
    onEnqueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp,
) {
    var hasFocus by remember {
        mutableStateOf(false)
    }
    val shape = RoundedCornerShape(cornerRadius)

    val backgroundColor = if (hasFocus) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Transparent
    }

    val borderColor = if (hasFocus) {
        MaterialTheme.colorScheme.border
    } else {
        Color.Transparent
    }
    val elevation = if (hasFocus) {
        10.dp
    } else {
        0.dp
    }

    EpisodeListItemContentLayer(
        playerEpisode = playerEpisode,
        onEpisodeSelected = onEpisodeSelected,
        onInfoClicked = onInfoClicked,
        onEnqueueClicked = onEnqueueClicked,
        modifier = modifier
            .clip(shape)
            .onFocusChanged {
                hasFocus = it.hasFocus
            }
            .border(borderWidth, borderColor, shape)
            .background(backgroundColor)
            .shadow(elevation, shape)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 16.dp)
    )
}

@Composable
private fun EpisodeListItemContentLayer(
    playerEpisode: PlayerEpisode,
    onEpisodeSelected: () -> Unit,
    onInfoClicked: () -> Unit,
    onEnqueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val duration = playerEpisode.duration
    val playButton = remember { FocusRequester() }
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.tiny),
        ) {
            EpisodeTitle(playerEpisode)
            Row(
                horizontalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.default),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = JetcasterAppDefaults.gap.paragraph)
            ) {
                PlayButton(
                    onClick = onEpisodeSelected,
                    modifier = Modifier.focusRequester(playButton)
                )
                if (duration != null) {
                    EpisodeDataAndDuration(playerEpisode.published, duration)
                }
                Spacer(modifier = Modifier.weight(1f))
                EnqueueButton(onClick = onEnqueueClicked)
                InfoButton(onClick = onInfoClicked)
            }
        }
    }
}

@Composable
private fun EpisodeTitle(playerEpisode: PlayerEpisode, modifier: Modifier = Modifier) {
    Text(
        text = playerEpisode.title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}
