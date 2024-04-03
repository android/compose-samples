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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.example.jetcaster.tv.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) =
    ButtonWithIcon(
        icon = Icons.Outlined.PlayArrow,
        label = stringResource(R.string.label_play),
        onClick = onClick,
        modifier = modifier
    )

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun EnqueueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.AutoMirrored.Filled.PlaylistAdd,
            contentDescription = stringResource(R.string.label_add_playlist),
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun ButtonWithIcon(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) =
    Button(onClick = onClick, modifier = modifier) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .width(ButtonDefaults.IconSize)
                .padding(end = ButtonDefaults.IconSpacing)
        )
        Text(text = label)
    }
