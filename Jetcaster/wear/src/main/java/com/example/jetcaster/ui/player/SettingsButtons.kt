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

package com.example.jetcaster.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jetcaster.R
import com.google.android.horologist.audio.ui.VolumeUiState
import com.google.android.horologist.audio.ui.components.SettingsButtonsDefaults
import com.google.android.horologist.audio.ui.components.actions.SetVolumeButton

/**
 * Settings buttons for the Jetcaster media app.
 * Favorite item and Set Volume.
 */
@Composable
fun SettingsButtons(
    volumeUiState: VolumeUiState,
    onVolumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(0.8124f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // FavoriteButton()

        SettingsButtonsDefaults.BrandIcon(
            iconId = R.drawable.ic_logo,
            enabled = enabled,
        )

        SetVolumeButton(
            onVolumeClick = onVolumeClick,
            volumeUiState = volumeUiState,
        )
    }
}
