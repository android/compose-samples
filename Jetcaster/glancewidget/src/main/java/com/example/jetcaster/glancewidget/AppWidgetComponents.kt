package com.example.jetcaster.glancewidget

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.layout.ContentScale
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Uses Coil to load images.
 */
@Composable
internal fun WidgetAsyncImage(
    uri: Uri,
    circleCrop: Boolean = false,
    contentDescription: String?,
    modifier: GlanceModifier = GlanceModifier,
) {
    val placeholderProvider = remember { ImageProvider(R.drawable.empty_round) }
    var imageProvider: ImageProvider by remember { mutableStateOf(placeholderProvider) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sizePx: Int = if (circleCrop) 400 else 200

    LaunchedEffect(key1 = uri) {

        val transformations = if (circleCrop) {
            listOf(CircleCropTransformation())
        } else {
            listOf()
        }

        val backup = com.example.jetcaster.core.designsystem.R.drawable.img_empty
        val request = ImageRequest.Builder(context)
            .data(uri)
            .placeholder(backup)
            .fallback(backup)
            .size(sizePx, sizePx)
            .target(
                onStart = { _ ->
                    imageProvider = placeholderProvider
                },
                onError = { data: Drawable? ->
                    imageProvider = placeholderProvider
                },
                onSuccess = { data: Drawable ->
                    val bitmap = (data as BitmapDrawable).bitmap
                    imageProvider = ImageProvider(bitmap)
                }
            )
            .transformations(transformations)
            .build()

        scope.launch(Dispatchers.IO) {
            val result = ImageLoader(context).execute(request)
            if (result is ErrorResult) {
                val t = result.throwable
                Log.e(TAG, "Image request error:", t)
            }
        }
    }

    Image(
        provider = imageProvider,
        contentDescription = contentDescription,
        contentScale = if (circleCrop) ContentScale.Fit else ContentScale.FillBounds,
        modifier = modifier
    )
}

internal enum class PlayPauseIcon {
    Play,
    Pause;

    companion object {
        fun from(isPlaying: Boolean) = if (isPlaying) Pause else Play
    }
}

@Composable
internal fun PlayPauseButton(
    state: PlayPauseIcon,
    onClick: () -> Unit,
    modifier: GlanceModifier = GlanceModifier
) {
    val (iconRes: Int, description: Int) = when (state) {
        PlayPauseIcon.Play -> R.drawable.outline_play_arrow_24 to R.string.content_description_play
        PlayPauseIcon.Pause -> R.drawable.outline_pause_24 to R.string.content_description_pause
    }

    val provider = ImageProvider(iconRes)
    val contentDescription = LocalContext.current.getString(description)

    SquareIconButton(
        provider,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier
    )
}

data class JetcasterAppWidgetViewState(
    val episodeTitle: String,
    val podcastTitle: String,
    val isPlaying: Boolean,
    val albumArtUri: String,
    val useDynamicColor: Boolean
)
