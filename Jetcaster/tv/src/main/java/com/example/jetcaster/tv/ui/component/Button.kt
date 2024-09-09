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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ButtonScale
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.example.jetcaster.tv.R

@Composable
internal fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    scale: ButtonScale = ButtonDefaults.scale(),
) =
    ButtonWithIcon(
        icon = Icons.Outlined.PlayArrow,
        label = stringResource(R.string.label_play),
        onClick = onClick,
        modifier = modifier,
        scale = scale
    )

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

@Composable
internal fun InfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = stringResource(R.string.label_info),
        )
    }
}

@Composable
internal fun PreviousButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Default.SkipPrevious,
            contentDescription = stringResource(R.string.label_previous_episode)
        )
    }
}

@Composable
internal fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Default.SkipNext,
            contentDescription = stringResource(R.string.label_next_episode)
        )
    }
}

@Composable
internal fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, description) = if (isPlaying) {
        Icons.Default.Pause to stringResource(R.string.label_pause)
    } else {
        Icons.Default.PlayArrow to stringResource(R.string.label_play)
    }
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(icon, description, modifier = Modifier.size(48.dp))
    }
}

@Composable
internal fun RewindButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Default.Replay10,
            contentDescription = stringResource(R.string.label_rewind)
        )
    }
}

@Composable
internal fun SkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Default.Forward10,
            contentDescription = stringResource(R.string.label_skip)
        )
    }
}
