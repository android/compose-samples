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

package com.example.jetcaster.ui.home.library

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetcaster.R
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.theme.Keyline1
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.util.fullWidthItem

fun LazyListScope.libraryItems(
    library: LibraryInfo,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit
) {
    item {
        Text(
            text = stringResource(id = R.string.latest_episodes),
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp,
            ),
            style = MaterialTheme.typography.titleLarge,
        )
    }

    items(
        library,
        key = { it.episode.uri }
    ) { item ->
        EpisodeListItem(
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillParentMaxWidth(),
        )
    }
}

fun LazyGridScope.libraryItems(
    library: LibraryInfo,
    navigateToPlayer: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit
) {
    fullWidthItem {
        Text(
            text = stringResource(id = R.string.latest_episodes),
            modifier = Modifier.padding(
                start = Keyline1,
                top = 16.dp,
            ),
            style = MaterialTheme.typography.headlineLarge,
        )
    }

    items(
        library,
        key = { it.episode.uri }
    ) { item ->
        EpisodeListItem(
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            onQueueEpisode = onQueueEpisode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
