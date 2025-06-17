/*
 * Copyright 2024-2025 The Android Open Source Project
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

package com.example.jetcaster.glancewidget

import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal val TAG = "JetcasterAppWidget"

/**
 * Implementation of App Widget functionality.
 */
class JetcasterAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = JetcasterAppWidget()
}

data class JetcasterAppWidgetViewState(val episodeTitle: String, val podcastTitle: String, val isPlaying: Boolean, val albumArtUri: String)

private object Sizes {
    val short = 72.dp
    val minWidth = 140.dp
    val smallBucketCutoffWidth = 250.dp // anything from minWidth to this will have no title

    val normal = 80.dp
    val medium = 56.dp
    val condensed = 48.dp
}

private enum class SizeBucket { Invalid, Narrow, Normal, NarrowShort, NormalShort }

@Composable
private fun calculateSizeBucket(): SizeBucket {
    val size: DpSize = LocalSize.current
    val width = size.width
    val height = size.height

    return when {
        width < Sizes.minWidth -> SizeBucket.Invalid
        width <= Sizes.smallBucketCutoffWidth ->
            if (height >= Sizes.short) SizeBucket.Narrow else SizeBucket.NarrowShort
        else ->
            if (height >= Sizes.short) SizeBucket.Normal else SizeBucket.NormalShort
    }
}

class JetcasterAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val testState = JetcasterAppWidgetViewState(
            episodeTitle =
            "100 - Android 15 DP 1, Stable Studio Iguana, Cloud Photo Picker, and more!",
            podcastTitle = "Now in Android",
            isPlaying = false,
            albumArtUri = "https://static.libsyn.com/p/assets/9/f/f/3/" +
                "9ff3cb5dc6cfb3e2e5bbc093207a2619/NIA000_PodcastThumbnail.png",
        )

        provideContent {
            val sizeBucket = calculateSizeBucket()
            val playPauseIcon = if (testState.isPlaying) PlayPauseIcon.Pause else PlayPauseIcon.Play
            val artUri = Uri.parse(testState.albumArtUri)

            GlanceTheme {
                when (sizeBucket) {
                    SizeBucket.Invalid -> WidgetUiInvalidSize()
                    SizeBucket.Narrow -> Widget(
                        iconSize = Sizes.medium,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                    )

                    SizeBucket.Normal -> WidgetUiNormal(
                        iconSize = Sizes.normal,
                        title = testState.episodeTitle,
                        subtitle = testState.podcastTitle,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                    )

                    SizeBucket.NarrowShort -> Widget(
                        iconSize = Sizes.condensed,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                    )

                    SizeBucket.NormalShort -> WidgetUiNormal(
                        iconSize = Sizes.condensed,
                        title = testState.episodeTitle,
                        subtitle = testState.podcastTitle,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetUiNormal(title: String, subtitle: String, imageUri: Uri, playPauseIcon: PlayPauseIcon, iconSize: Dp) {
    Scaffold {
        Row(
            GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            AlbumArt(imageUri, GlanceModifier.size(iconSize))
            PodcastText(title, subtitle, modifier = GlanceModifier.padding(16.dp).defaultWeight())
            PlayPauseButton(GlanceModifier.size(iconSize), playPauseIcon, {})
        }
    }
}

@Composable
private fun Widget(iconSize: Dp, imageUri: Uri, playPauseIcon: PlayPauseIcon) {
    /* title bar will be optional in scaffold in glance 1.1.0-beta3*/
    Scaffold(titleBar = {}) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            AlbumArt(imageUri, GlanceModifier.size(iconSize))
            Spacer(GlanceModifier.defaultWeight())
            PlayPauseButton(GlanceModifier.size(iconSize), playPauseIcon, {})
        }
    }
}

@Composable
private fun WidgetUiInvalidSize() {
    Box(modifier = GlanceModifier.fillMaxSize().background(ColorProvider(Color.Magenta))) {
        Text("invalid size")
    }
}

@Composable
private fun AlbumArt(imageUri: Uri, modifier: GlanceModifier = GlanceModifier) {
    WidgetAsyncImage(uri = imageUri, contentDescription = null, modifier = modifier)
}

@Composable
fun PodcastText(title: String, subtitle: String, modifier: GlanceModifier = GlanceModifier) {
    val fgColor = GlanceTheme.colors.onPrimaryContainer
    val size = LocalSize.current
    when {
        size.height >= Sizes.short -> Column(modifier) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = fgColor,
                ),
                maxLines = 2,
            )
            Text(
                text = subtitle,
                style = TextStyle(fontSize = 14.sp, color = fgColor),
                maxLines = 2,
            )
        }
        else -> Column(modifier) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = fgColor,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun PlayPauseButton(modifier: GlanceModifier = GlanceModifier.size(Sizes.normal), state: PlayPauseIcon, onClick: () -> Unit) {
    val (iconRes: Int, description: Int) = when (state) {
        PlayPauseIcon.Play -> R.drawable.outline_play_arrow_24 to R.string.content_description_play
        PlayPauseIcon.Pause -> R.drawable.outline_pause_24 to R.string.content_description_pause
    }

    val provider = ImageProvider(iconRes)
    val contentDescription = LocalContext.current.getString(description)

    SquareIconButton(
        modifier = modifier,
        imageProvider = provider,
        contentDescription = contentDescription,
        onClick = onClick,
    )
}

enum class PlayPauseIcon { Play, Pause }

/**
 * Uses Coil to load images.
 */
@Composable
private fun WidgetAsyncImage(uri: Uri, contentDescription: String?, modifier: GlanceModifier = GlanceModifier) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = uri) {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .size(200, 200)
            .target { data: Drawable ->
                bitmap = (data as BitmapDrawable).bitmap
            }
            .build()

        scope.launch(Dispatchers.IO) {
            val result = ImageLoader(context).execute(request)
            if (result is ErrorResult) {
                val t = result.throwable
                Log.e(TAG, "Image request error:", t)
            }
        }
    }

    bitmap?.let { bitmap ->
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.cornerRadius(12.dp), // TODO: confirm radius with design
        )
    }
}
