package com.example.jetcaster.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.R
import com.example.jetcaster.core.data.database.model.Episode
import com.example.jetcaster.core.data.database.model.Podcast
import com.example.jetcaster.core.data.model.EpisodeInfo
import com.example.jetcaster.core.data.model.PlayerEpisode
import com.example.jetcaster.core.data.model.PodcastInfo
import com.example.jetcaster.core.data.model.asExternalModel
import com.example.jetcaster.designsystem.theme.Keyline1
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Deprecated("Use the EpisodeListItem overload that accepts external models instead.")
@Composable
fun EpisodeListItem(
    episode: Episode,
    podcast: Podcast,
    onClick: (String) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    showPodcastImage: Boolean = true,
) {
    EpisodeListItem(
        episode = episode.asExternalModel(),
        podcast = podcast.asExternalModel(),
        onClick = onClick,
        onQueueEpisode = onQueueEpisode,
        modifier = modifier,
        showDivider = showDivider,
        showPodcastImage = showPodcastImage
    )
}

@Composable
fun EpisodeListItem(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onClick: (String) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    showPodcastImage: Boolean = true,
) {
    ConstraintLayout(
        modifier = modifier.clickable {
            onClick(podcast.uri)
        }
    ) {
        val (
            divider, episodeTitle, podcastTitle, image, playIcon,
            date, addPlaylist, overflow
        ) = createRefs()

        if (showDivider) {
            HorizontalDivider(
                Modifier.constrainAs(divider) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                    width = Dimension.fillToConstraints
                }
            )
        }

        // If we have an image Url, we can show it using Coil
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(podcast.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .constrainAs(image) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                    visibility = if (showPodcastImage) Visibility.Visible else Visibility.Gone
                },
        )

        Text(
            text = episode.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.constrainAs(episodeTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = Keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)
                height = Dimension.preferredWrapContent
                width = Dimension.preferredWrapContent
            }
        )

        val titleImageBarrier = createBottomBarrier(podcastTitle, image)

        Text(
            text = podcast.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.constrainAs(podcastTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = Keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(episodeTitle.bottom, 6.dp)
                height = Dimension.preferredWrapContent
                width = Dimension.preferredWrapContent
            }
        )

        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) { /* TODO */ }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
                .constrainAs(playIcon) {
                    start.linkTo(parent.start, Keyline1)
                    top.linkTo(titleImageBarrier, margin = 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
        )

        val duration = episode.duration
        Text(
            text = when {
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
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(date) {
                centerVerticallyTo(playIcon)
                linkTo(
                    start = playIcon.end,
                    startMargin = 12.dp,
                    end = addPlaylist.start,
                    endMargin = 16.dp,
                    bias = 0f // float this towards the start
                )
                width = Dimension.preferredWrapContent
            }
        )

        IconButton(
            onClick = {
                onQueueEpisode(
                    PlayerEpisode(
                        podcastInfo = podcast,
                        episodeInfo = episode
                    )
                )
            },
            modifier = Modifier.constrainAs(addPlaylist) {
                end.linkTo(overflow.start)
                centerVerticallyTo(playIcon)
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.constrainAs(overflow) {
                end.linkTo(parent.end, 8.dp)
                centerVerticallyTo(playIcon)
            }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
