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

package com.example.jetcaster.tv.ui.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.player.EpisodePlayerState
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.R
import com.example.jetcaster.tv.model.EpisodeList
import com.example.jetcaster.tv.ui.component.BackgroundContainer
import com.example.jetcaster.tv.ui.component.EnqueueButton
import com.example.jetcaster.tv.ui.component.EpisodeDetails
import com.example.jetcaster.tv.ui.component.EpisodeRow
import com.example.jetcaster.tv.ui.component.InfoButton
import com.example.jetcaster.tv.ui.component.Loading
import com.example.jetcaster.tv.ui.component.NextButton
import com.example.jetcaster.tv.ui.component.PlayPauseButton
import com.example.jetcaster.tv.ui.component.PreviousButton
import com.example.jetcaster.tv.ui.component.RewindButton
import com.example.jetcaster.tv.ui.component.Seekbar
import com.example.jetcaster.tv.ui.component.SkipButton
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(
    backToHome: () -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    playScreenViewModel: PlayerScreenViewModel = hiltViewModel()
) {
    val uiState by playScreenViewModel.uiStateFlow.collectAsStateWithLifecycle()

    when (val s = uiState) {
        PlayerScreenUiState.Loading -> Loading(modifier)
        PlayerScreenUiState.NoEpisodeInQueue -> {
            NoEpisodeInQueue(backToHome = backToHome, modifier = modifier)
        }

        is PlayerScreenUiState.Ready -> {
            Player(
                episodePlayerState = s.playerState,
                play = playScreenViewModel::play,
                pause = playScreenViewModel::pause,
                previous = playScreenViewModel::previous,
                next = playScreenViewModel::next,
                skip = playScreenViewModel::skip,
                rewind = playScreenViewModel::rewind,
                enqueue = playScreenViewModel::enqueue,
                playEpisode = playScreenViewModel::play,
                showDetails = showDetails,
            )
        }
    }
}

@Composable
private fun Player(
    episodePlayerState: EpisodePlayerState,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    autoStart: Boolean = true
) {
    LaunchedEffect(key1 = autoStart) {
        if (autoStart && !episodePlayerState.isPlaying) {
            play()
        }
    }

    val currentEpisode = episodePlayerState.currentEpisode

    if (currentEpisode != null) {
        EpisodePlayerWithBackground(
            playerEpisode = currentEpisode,
            queue = EpisodeList(episodePlayerState.queue),
            isPlaying = episodePlayerState.isPlaying,
            timeElapsed = episodePlayerState.timeElapsed,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            enqueue = enqueue,
            showDetails = showDetails,
            playEpisode = playEpisode,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodePlayerWithBackground(
    playerEpisode: PlayerEpisode,
    queue: EpisodeList,
    isPlaying: Boolean,
    timeElapsed: Duration,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    playEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    val episodePlayer = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        episodePlayer.requestFocus()
    }

    BackgroundContainer(
        playerEpisode = playerEpisode,
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        EpisodePlayer(
            playerEpisode = playerEpisode,
            isPlaying = isPlaying,
            timeElapsed = timeElapsed,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            enqueue = enqueue,
            showDetails = showDetails,
            focusRequester = episodePlayer,
            modifier = Modifier
                .padding(JetcasterAppDefaults.overScanMargin.player.intoPaddingValues())
        )

        PlayerQueueOverlay(
            playerEpisodeList = queue,
            onSelected = playEpisode,
            modifier = Modifier.fillMaxSize(),
            contentPadding = JetcasterAppDefaults.overScanMargin.player.copy(top = 0.dp)
                .intoPaddingValues(),
            offset = DpOffset(0.dp, 136.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodePlayer(
    playerEpisode: PlayerEpisode,
    isPlaying: Boolean,
    timeElapsed: Duration,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    enqueue: (PlayerEpisode) -> Unit,
    showDetails: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester = remember { BringIntoViewRequester() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.section),
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged {
                if (it.hasFocus) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
            .then(modifier)
    ) {
        EpisodeDetails(
            playerEpisode = playerEpisode,
            content = {},
            controls = {
                EpisodeControl(
                    showDetails = { showDetails(playerEpisode) },
                    enqueue = { enqueue(playerEpisode) }
                )
            },
        )
        PlayerControl(
            isPlaying = isPlaying,
            timeElapsed = timeElapsed,
            length = playerEpisode.duration,
            play = play,
            pause = pause,
            previous = previous,
            next = next,
            skip = skip,
            rewind = rewind,
            focusRequester = focusRequester
        )
    }
}

@Composable
private fun EpisodeControl(
    showDetails: () -> Unit,
    enqueue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.item)
    ) {
        EnqueueButton(
            onClick = enqueue,
            modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.default.intoDpSize())
        )
        InfoButton(
            onClick = showDetails,
            modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.default.intoDpSize())
        )
    }
}

@Composable
private fun PlayerControl(
    isPlaying: Boolean,
    timeElapsed: Duration,
    length: Duration?,
    play: () -> Unit,
    pause: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    skip: () -> Unit,
    rewind: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val playPauseButton = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                JetcasterAppDefaults.gap.default,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        playPauseButton.requestFocus()
                    }
                }
                .focusable(),
        ) {
            PreviousButton(
                onClick = previous,
                modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            RewindButton(
                onClick = rewind,
                modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = {
                    if (isPlaying) {
                        pause()
                    } else {
                        play()
                    }
                },
                modifier = Modifier
                    .size(JetcasterAppDefaults.iconButtonSize.large.intoDpSize())
                    .focusRequester(playPauseButton)
            )
            SkipButton(
                onClick = skip,
                modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
            NextButton(
                onClick = next,
                modifier = Modifier.size(JetcasterAppDefaults.iconButtonSize.medium.intoDpSize())
            )
        }
        if (length != null) {
            ElapsedTimeIndicator(timeElapsed, length, skip, rewind)
        }
    }
}

@Composable
private fun ElapsedTimeIndicator(
    timeElapsed: Duration,
    length: Duration,
    skip: () -> Unit,
    rewind: () -> Unit,
    modifier: Modifier = Modifier,
    knobSize: Dp = 8.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(JetcasterAppDefaults.gap.tiny)
    ) {
        ElapsedTime(timeElapsed = timeElapsed, length = length)
        Seekbar(
            timeElapsed = timeElapsed,
            length = length,
            knobSize = knobSize,
            onMoveLeft = rewind,
            onMoveRight = skip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ElapsedTime(
    timeElapsed: Duration,
    length: Duration,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    val elapsed =
        stringResource(
            R.string.minutes_seconds,
            timeElapsed.toMinutes(),
            timeElapsed.toSeconds() % 60
        )
    val l =
        stringResource(R.string.minutes_seconds, length.toMinutes(), length.toSeconds() % 60)
    Text(
        text = stringResource(R.string.elapsed_time, elapsed, l),
        style = style,
        modifier = modifier
    )
}

@Composable
private fun NoEpisodeInQueue(
    backToHome: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Column {
            Text(
                text = stringResource(R.string.display_nothing_in_queue),
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(JetcasterAppDefaults.gap.paragraph))
            Text(text = stringResource(R.string.message_nothing_in_queue))
            Button(onClick = backToHome, modifier = Modifier.focusRequester(focusRequester)) {
                Text(text = stringResource(R.string.label_back_to_home))
            }
        }
    }
}

@Composable
private fun PlayerQueueOverlay(
    playerEpisodeList: EpisodeList,
    onSelected: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
    contentPadding: PaddingValues = PaddingValues(),
    contentAlignment: Alignment = Alignment.BottomStart,
    scrim: DrawScope.() -> Unit = {
        val brush = Brush.verticalGradient(
            listOf(Color.Transparent, Color.Black),
        )
        drawRect(brush, blendMode = BlendMode.Multiply)
    },
    offset: DpOffset = DpOffset.Zero,
) {
    var hasFocus by remember { mutableStateOf(false) }
    val actualOffset = if (hasFocus) {
        DpOffset.Zero
    } else {
        offset
    }
    Box(
        modifier = modifier.drawWithCache {
            onDrawBehind {
                if (hasFocus) {
                    scrim()
                }
            }
        },
        contentAlignment = contentAlignment,
    ) {
        EpisodeRow(
            playerEpisodeList = playerEpisodeList,
            onSelected = onSelected,
            horizontalArrangement = horizontalArrangement,
            contentPadding = contentPadding,
            modifier = Modifier
                .offset(actualOffset.x, actualOffset.y)
                .onFocusChanged { hasFocus = it.hasFocus }
        )
    }
}
