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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import coil.compose.AsyncImage
import com.example.jetcaster.R
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.ui.preview.WearPreviewEpisodes
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MediaContent(
    episode: PlayerEpisode,
    onItemClick: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    episodeArtworkPlaceholder: Painter = painterResource(id = R.drawable.music),
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
                duration.toMinutes().toInt(),
            )
        }
        // Otherwise we just use the date
        else -> MediumDateFormatter.format(episode.published)
    }

    FilledTonalButton(
        label = {
            Text(
                mediaTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onClick = { onItemClick(episode) },
        secondaryLabel = {
            Text(
                secondaryLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        icon = {
            AsyncImage(
                model = episode.podcastImageUrl,
                contentDescription = mediaTitle,
                error = episodeArtworkPlaceholder,
                placeholder = episodeArtworkPlaceholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(
                        ButtonDefaults.LargeIconSize,
                    )
                    .clip(CircleShape),

            )
        },
        modifier = modifier.fillMaxWidth(),
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MediaContentPreview(
    @PreviewParameter(WearPreviewEpisodes::class)
    episode: PlayerEpisode,
    modifier: Modifier = Modifier,
) {
    AppScaffold(modifier = modifier) {
        val contentPadding = rememberResponsiveColumnPadding(
            first = ColumnItemType.Button,
        )

        ScreenScaffold(contentPadding = contentPadding) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
            ) {
                MediaContent(
                    episode, onItemClick = { null },
                )
            }
        }
    }
}

val MediumDateFormatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
