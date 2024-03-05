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

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.ui.home.category.EpisodeListItem

fun LazyListScope.libraryItems(
    episodes: List<EpisodeToPodcast>,
    navigateToPlayer: (String) -> Unit
) {
    if (episodes.isEmpty()) {
        // TODO: Empty state
        return
    }

    items(episodes, key = { it.episode.uri }) { item ->
        EpisodeListItem(
            episode = item.episode,
            podcast = item.podcast,
            onClick = navigateToPlayer,
            modifier = Modifier.fillParentMaxWidth()
        )
    }
}
