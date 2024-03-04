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
