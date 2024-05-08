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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.ChipDefaults
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.coil.CoilPaintable
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MediaContent(
    episode: PlayerEpisode,
    episodeArtworkPlaceholder: Painter?,
    onItemClick: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaTitle = episode.title
    val duration = episode.duration

    val secondaryLabel = when {
        duration != null -> {
            // If we have the duration, we combine the date/duration via a
            // formatted string
            stringResource(
                R.string.episode_date_duration,
                MediumDateFormatter.format(episode.published),
                duration.toMinutes().toInt()
            )
        }
        // Otherwise we just use the date
        else -> MediumDateFormatter.format(episode.published)
    }

    Chip(
        label = mediaTitle,
        onClick = { onItemClick(episode) },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(episode.podcastImageUrl, episodeArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
        modifier = modifier
    )
}

public val MediumDateFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
