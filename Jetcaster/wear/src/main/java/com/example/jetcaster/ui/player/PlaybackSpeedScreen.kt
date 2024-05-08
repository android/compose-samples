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

package com.example.jetcaster.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.Text
import com.example.jetcaster.R
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.listTextPadding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ResponsiveListHeader

/**
 * Playback Speed Screen with an [InlineSlider].
 */
@Composable
public fun PlaybackSpeedScreen(
    modifier: Modifier = Modifier,
    playbackSpeedViewModel: PlaybackSpeedViewModel = hiltViewModel(),
) {
    val playbackSpeedUiState by playbackSpeedViewModel.speedUiState.collectAsState()

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(modifier = Modifier.listTextPadding()) {
                    Text(stringResource(R.string.speed))
                }
            }
            item {
                PlaybackSpeedScreen(
                    playbackSpeedUiState = playbackSpeedUiState,
                    increasePlaybackSpeed = playbackSpeedViewModel::increaseSpeed,
                    decreasePlaybackSpeed = playbackSpeedViewModel::decreaseSpeed,
                    modifier = modifier
                )
            }

            item {
                Text(
                    text = String.format("%.1fx", playbackSpeedUiState.current),
                )
            }
        }
    }
}

@Composable
internal fun PlaybackSpeedScreen(
    playbackSpeedUiState: PlaybackSpeedUiState,
    increasePlaybackSpeed: () -> Unit,
    decreasePlaybackSpeed: () -> Unit,
    modifier: Modifier
) {
    InlineSlider(
        value = playbackSpeedUiState.current,
        onValueChange = {
            if (it > playbackSpeedUiState.current) increasePlaybackSpeed()
            else if (it > 0.5) decreasePlaybackSpeed()
        },
        increaseIcon = {
            Icon(
                InlineSliderDefaults.Increase,
                stringResource(R.string.increase_playback_speed)
            )
        },
        decreaseIcon = {
            CompositionLocalProvider(
                LocalContentAlpha provides
                    if (playbackSpeedUiState.current > 1f)
                        LocalContentAlpha.current else ContentAlpha.disabled
            ) {
                Icon(
                    InlineSliderDefaults.Decrease,
                    stringResource(R.string.decrease_playback_speed)
                )
            }
        },
        valueRange = 0.5f..2f,
        steps = 2,
        segmented = true
    )
}
