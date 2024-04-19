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

package com.example.jetcaster.tv.ui.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.example.jetcaster.core.data.database.model.toPlayerEpisode
import com.example.jetcaster.core.model.PlayerEpisode
import com.example.jetcaster.tv.ui.component.Background
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDataAndDuration
import com.example.jetcaster.tv.ui.component.ErrorState
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.PlayButton
import com.example.jetcaster.tv.ui.component.Thumbnail
import com.example.jetcaster.tv.ui.component.TwoColumn
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
fun EpisodeScreen(
    playEpisode: () -> Unit,
    backToHome: () -> Unit,
    modifier: Modifier = Modifier,
    episodeScreenViewModel: EpisodeScreenViewModel = hiltViewModel()
) {

    val uiState by episodeScreenViewModel.uiStateFlow.collectAsState()

    when (val s = uiState) {
        EpisodeScreenUiState.Loading -> Loading(modifier = modifier)
        EpisodeScreenUiState.Error -> ErrorState(backToHome = backToHome, modifier = modifier)
        is EpisodeScreenUiState.Ready -> EpisodeDetailsWithBackground(
            episodeToPodcast = s.episodeToPodcast,
            playEpisode = {
                episodeScreenViewModel.play(it)
                playEpisode()
            },
            addPlayList = episodeScreenViewModel::addPlayList
        )
    }
}

@Composable
private fun EpisodeDetailsWithBackground(
    episodeToPodcast: EpisodeToPodcast,
    playEpisode: (PlayerEpisode) -> Unit,
    addPlayList: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Background(podcast = episodeToPodcast.podcast, modifier = Modifier.fillMaxSize())
        EpisodeDetails(
            episodeToPodcast = episodeToPodcast,
            playEpisode = playEpisode,
            addPlayList = addPlayList,
            modifier = Modifier
                .padding(JetcasterAppDefaults.overScanMargin.episode.intoPaddingValues())
        )
    }
}

@Composable
private fun EpisodeDetails(
    episodeToPodcast: EpisodeToPodcast,
    playEpisode: (PlayerEpisode) -> Unit,
    addPlayList: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
) {
    TwoColumn(
        first = {
            Thumbnail(
                podcast = episodeToPodcast.podcast,
                size = JetcasterAppDefaults.thumbnailSize.episode
            )
        },
        second = {
            EpisodeInfo(
                episode = episodeToPodcast.episode,
                playEpisode = { playEpisode(episodeToPodcast.toPlayerEpisode()) },
                addPlayList = { addPlayList(episodeToPodcast.toPlayerEpisode()) },
                modifier = Modifier.weight(1f)
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeInfo(
    episode: Episode,
    playEpisode: () -> Unit,
    addPlayList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val author = episode.author
    val duration = episode.duration
    val summary = episode.summary

    Column(modifier) {
        if (author != null) {
            Text(text = author, style = MaterialTheme.typography.bodySmall)
        }
        Text(text = episode.title, style = MaterialTheme.typography.headlineLarge)
        if (duration != null) {
            EpisodeDataAndDuration(offsetDateTime = episode.published, duration = duration)
        }
        if (summary != null) {
            Spacer(modifier = Modifier.height(JetcasterAppDefaults.gap.paragraph))
            Text(text = summary, softWrap = true, maxLines = 5, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.height(JetcasterAppDefaults.gap.paragraph))
        Controls(playEpisode = playEpisode, addPlayList = addPlayList)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Controls(
    playEpisode: () -> Unit,
    addPlayList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PlayButton(onClick = playEpisode)
        EnqueueButton(onClick = addPlayList)
    }
}
