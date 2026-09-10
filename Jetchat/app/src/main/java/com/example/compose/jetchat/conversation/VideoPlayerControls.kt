/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat.conversation

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.MuteButtonState
import androidx.media3.ui.compose.state.PlayPauseButtonState
import androidx.media3.ui.compose.state.ProgressStateWithTickInterval
import com.example.compose.jetchat.R
import java.util.Locale

/**
 * Overlay controls for VideoPlayer with frosted glass styling and blur underneath.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerOverlayControls(
    playPauseButtonState: PlayPauseButtonState,
    muteButtonState: MuteButtonState,
    progressState: ProgressStateWithTickInterval,
    isFullscreen: Boolean = true,
    blurRegionSpecs: MutableMap<String, BlurRegionSpec>,
    onUserInteraction: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    onUpdateBlurRegions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        // Top-End Exit Fullscreen Button (fullscreen only)
        if (isFullscreen) {
            VideoPlayerExitFullscreenButton(
                onClick = {
                    onUserInteraction()
                    onToggleFullscreen()
                },
                onUpdateRegion = { spec ->
                    blurRegionSpecs[spec.id] = spec
                    onUpdateBlurRegions()
                },
                onRemoveRegion = { id ->
                    blurRegionSpecs.remove(id)
                    onUpdateBlurRegions()
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            )
        }

        // Center Play/Pause Floating Circular Button
        VideoPlayerCenterPlayButton(
            playPauseButtonState = playPauseButtonState,
            onUserInteraction = onUserInteraction,
            onUpdateRegion = { spec ->
                blurRegionSpecs[spec.id] = spec
                onUpdateBlurRegions()
            },
            onRemoveRegion = { id ->
                blurRegionSpecs.remove(id)
                onUpdateBlurRegions()
            },
            modifier = Modifier.align(Alignment.Center),
        )

        // Bottom Control Bar Pill
        VideoPlayerBottomBar(
            playPauseButtonState = playPauseButtonState,
            muteButtonState = muteButtonState,
            progressState = progressState,
            isFullscreen = isFullscreen,
            onUserInteraction = onUserInteraction,
            onSeekTo = onSeekTo,
            onToggleFullscreen = onToggleFullscreen,
            onUpdateRegion = { spec ->
                blurRegionSpecs[spec.id] = spec
                onUpdateBlurRegions()
            },
            onRemoveRegion = { id ->
                blurRegionSpecs.remove(id)
                onUpdateBlurRegions()
            },
            modifier = Modifier.align(Alignment.BottomCenter).then(
                if (isFullscreen)
                    Modifier.windowInsetsPadding(WindowInsets.safeContent)
                else Modifier,
            ),
        )
    }
}

@Composable
private fun VideoPlayerExitFullscreenButton(
    onClick: () -> Unit,
    onUpdateRegion: (BlurRegionSpec) -> Unit,
    onRemoveRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val exitFullscreenDesc = stringResource(R.string.exit_fullscreen_video)
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .size(36.dp)
            .registerBlurRegion(
                id = "top_close",
                cornerRadius = 18.dp,
                onUpdateRegion = onUpdateRegion,
                onRemoveRegion = onRemoveRegion,
            )
            .background(
                color = Color(0x33000000),
                shape = CircleShape,
            )
            .border(
                width = 1.dp,
                color = Color(0x33FFFFFF),
                shape = CircleShape,
            ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = exitFullscreenDesc,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerCenterPlayButton(
    playPauseButtonState: PlayPauseButtonState,
    onUserInteraction: () -> Unit,
    onUpdateRegion: (BlurRegionSpec) -> Unit,
    onRemoveRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showPlay = playPauseButtonState.showPlay
    val playDesc = stringResource(if (showPlay) R.string.play_video else R.string.pause_video)
    val rippleIndication = remember { ripple(bounded = true, radius = 36.dp) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(72.dp)
            .registerBlurRegion(
                id = "center_play",
                cornerRadius = 36.dp,
                onUpdateRegion = onUpdateRegion,
                onRemoveRegion = onRemoveRegion,
            )
            .clip(CircleShape)
            .background(
                color = Color(0x33000000),
                shape = CircleShape,
            )
            .border(
                width = 1.dp,
                color = Color(0x4DFFFFFF),
                shape = CircleShape,
            )
            .clickable(
                enabled = playPauseButtonState.isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = rippleIndication,
                role = Role.Button,
            ) {
                onUserInteraction()
                playPauseButtonState.onClick()
            }
            .semantics {
                contentDescription = playDesc
            },
    ) {
        Icon(
            painter = painterResource(
                id = if (showPlay) R.drawable.ic_play_arrow else R.drawable.ic_pause,
            ),
            contentDescription = playDesc,
            tint = Color.White,
            modifier = Modifier.size(30.dp),
        )
    }
}

private val BottomBarShape = RoundedCornerShape(16.dp)

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerBottomBar(
    playPauseButtonState: PlayPauseButtonState,
    muteButtonState: MuteButtonState,
    progressState: ProgressStateWithTickInterval,
    isFullscreen: Boolean,
    onUserInteraction: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    onUpdateRegion: (BlurRegionSpec) -> Unit,
    onRemoveRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showPlay = playPauseButtonState.showPlay
    val playDesc = stringResource(if (showPlay) R.string.play_video else R.string.pause_video)
    val showMuted = muteButtonState.showMuted
    val muteDesc = stringResource(if (showMuted) R.string.unmute_video else R.string.mute_video)
    val fullscreenDesc = stringResource(
        if (isFullscreen) R.string.exit_fullscreen_video else R.string.fullscreen_video,
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .registerBlurRegion(
                id = "bottom_bar",
                cornerRadius = 0.dp,
                onUpdateRegion = onUpdateRegion,
                onRemoveRegion = onRemoveRegion,
            )
            .background(
                color = Color(0x33000000),
                shape = BottomBarShape,
            )
            .border(
                width = 1.dp,
                color = Color(0x33FFFFFF),
                shape = BottomBarShape,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        // Play/pause icon button
        IconButton(
            onClick = {
                onUserInteraction()
                playPauseButtonState.onClick()
            },
            enabled = playPauseButtonState.isEnabled,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(
                    id = if (showPlay) R.drawable.ic_play_arrow else R.drawable.ic_pause,
                ),
                contentDescription = playDesc,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }

        // Isolated progress slider: reads fast-changing position state without recomposing the entire bottom bar
        VideoPlayerProgressSlider(
            progressState = progressState,
            onUserInteraction = onUserInteraction,
            onSeekTo = onSeekTo,
            modifier = Modifier.weight(1f),
        )

        // Mute / Unmute icon button
        IconButton(
            onClick = {
                onUserInteraction()
                muteButtonState.onClick()
            },
            enabled = muteButtonState.isEnabled,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(
                    id = if (showMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_up,
                ),
                contentDescription = muteDesc,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }

        // Fullscreen toggle icon button
        IconButton(
            onClick = {
                onUserInteraction()
                onToggleFullscreen()
            },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(
                    id = if (isFullscreen) R.drawable.ic_fullscreen_exit else R.drawable.ic_fullscreen,
                ),
                contentDescription = fullscreenDesc,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerProgressSlider(
    progressState: ProgressStateWithTickInterval,
    onUserInteraction: () -> Unit,
    onSeekTo: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentPositionMs = progressState.currentPositionMs.coerceAtLeast(0L).toInt()
    val durationMs = progressState.durationMs.coerceAtLeast(1L).toInt()
    val formattedTime = remember(currentPositionMs, durationMs) {
        "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}"
    }
    Text(
        text = formattedTime,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(horizontal = 4.dp),
    )

    val progressFraction = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    Slider(
        value = progressFraction,
        onValueChange = { progress ->
            onUserInteraction()
            onSeekTo(progress)
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
        ),
        modifier = modifier
            .height(24.dp)
            .padding(horizontal = 6.dp),
    )
}

private fun formatTime(millis: Int): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
