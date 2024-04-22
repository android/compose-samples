/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetcaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.example.jetcaster.R
import com.example.jetcaster.ui.player.PlayerUiState
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton
import com.google.android.horologist.audio.ui.components.actions.SettingsButton
import com.google.android.horologist.compose.material.IconRtlMode

/**
 * Settings buttons for the Jetcaster media app.
 * Add to queue and Set Volume.
 */
@Composable
fun SettingsButtons(
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    playerUiState: PlayerUiState,
    onPlaybackSpeedChange: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(0.8124f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PlaybackSpeedButton(
            currentPlayerSpeed = playerUiState.episodePlayerState
                .playbackSpeed.toMillis().toFloat() / 1000,
            onPlaybackSpeedChange = onPlaybackSpeedChange,
            enabled = enabled
        )

        SettingsButtonsDefaults.BrandIcon(
            iconId = R.drawable.ic_logo,
            enabled = enabled,
        )

        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeUiState = volumeUiState,
            enabled = enabled
        )
    }
}

@Composable
fun PlaybackSpeedButton(
    currentPlayerSpeed: Float,
    onPlaybackSpeedChange: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SettingsButton(
        modifier = modifier,
        onClick = onPlaybackSpeedChange,
        enabled = enabled,
        imageVector =
        when (currentPlayerSpeed) {
            1f -> ImageVector.vectorResource(R.drawable.speed_1x)
            1.5f -> ImageVector.vectorResource(R.drawable.speed_15x)
            else -> { ImageVector.vectorResource(R.drawable.speed_2x) }
        },
        iconRtlMode = IconRtlMode.Mirrored,
        contentDescription = stringResource(R.string.change_playback_speed_content_description),
    )
}
