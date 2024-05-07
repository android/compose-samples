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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.tv.ui.theme.JetcasterAppDefaults

@Composable
internal fun EpisodeDetails(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    controls: (@Composable () -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(JetcasterAppDefaults.gap.item),
    content: @Composable ColumnScope.() -> Unit
) {
    TwoColumn(
        modifier = modifier,
        first = {
            Thumbnail(
                playerEpisode,
                size = JetcasterAppDefaults.thumbnailSize.episodeDetails
            )
        },
        second = {
            Column(
                modifier = modifier,
                verticalArrangement = verticalArrangement
            ) {
                EpisodeAuthor(playerEpisode = playerEpisode)
                EpisodeTitle(playerEpisode = playerEpisode)
                content()
                if (controls != null) {
                    controls()
                }
            }
        }
    )
}

@Composable
internal fun EpisodeAuthor(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(text = playerEpisode.author, modifier = modifier, style = style)
}

@Composable
internal fun EpisodeTitle(
    playerEpisode: PlayerEpisode,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    Text(text = playerEpisode.title, modifier = modifier, style = style)
}
