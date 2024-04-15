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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.example.jetcaster.R
import com.example.jetcaster.ui.library.ButtonsContent
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.PlaceholderChip
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun LoadingEntityScreen(columnState: ScalingLazyColumnState) {
    EntityScreen(
        columnState = columnState,
        headerContent = {
            DefaultEntityScreenHeader(
                title = stringResource(id = R.string.loading)
            )
        },
        buttonsContent = {
            ButtonsContent(
                onChangeSpeedButtonClick = {},
                onPlayButtonClick = {},
            )
        },
        content = {
            items(count = 2) {
                PlaceholderChip(colors = ChipDefaults.secondaryChipColors())
            }
        }
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ButtonsContent(
    onChangeSpeedButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    enabled: Boolean = false
) {

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Button(
            imageVector = ImageVector.vectorResource(R.drawable.speed),
            contentDescription = stringResource(id = R.string.speed_button_content_description),
            onClick = { onChangeSpeedButtonClick() },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = { onPlayButtonClick },
            enabled = enabled,
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}
