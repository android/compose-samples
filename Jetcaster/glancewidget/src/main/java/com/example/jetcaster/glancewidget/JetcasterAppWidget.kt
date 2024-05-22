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

package com.example.jetcaster.glancewidget

import android.content.Context
import android.net.Uri
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

internal val TAG = "JetcasterAppWidegt"

/**
 * Implementation of App Widget functionality.
 */
class JetcasterAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = JetcasterAppWidget()
}

private object Sizes {
    val minWidth = 140.dp
    val smallBucketCutoffWidth = 250.dp // anything from minWidth to this will have no title

    val imageNormal = 80.dp
    val imageCondensed = 60.dp
}

private enum class SizeBucket { Invalid, Narrow, Normal }

@Composable
private fun calculateSizeBucket(): SizeBucket {
    val size: DpSize = LocalSize.current
    val width = size.width

    return when {
        width < Sizes.minWidth -> SizeBucket.Invalid
        width <= Sizes.smallBucketCutoffWidth -> SizeBucket.Narrow
        else -> SizeBucket.Normal
    }
}

class JetcasterAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val state = TEST_DATA

        provideContent {
            val sizeBucket = calculateSizeBucket()
            val playPauseIcon = if (state.isPlaying) PlayPauseIcon.Pause else PlayPauseIcon.Play
            val artUri = Uri.parse(state.albumArtUri)

            GlanceTheme(
                colors = ColorProviders(
                    light = lightColorScheme(),
                    dark = darkColorScheme()
                )
            ) {
                when (sizeBucket) {
                    SizeBucket.Invalid -> WidgetUiInvalidSize()
                    SizeBucket.Narrow -> WidgetUiNarrow(
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon
                    )

                    SizeBucket.Normal -> WidgetUiNormal(
                        title = state.episodeTitle,
                        subtitle = state.podcastTitle,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetUiNormal(
    title: String,
    subtitle: String,
    imageUri: Uri,
    playPauseIcon: PlayPauseIcon,
) {
    Scaffold(titleBar = {} /* title bar will be optional starting in glance 1.1.0-beta3*/) {
        Row(
            GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            AlbumArt(imageUri, GlanceModifier.size(Sizes.imageNormal))
            PodcastText(title, subtitle, modifier = GlanceModifier.padding(16.dp).defaultWeight())
            PlayPauseButton(playPauseIcon, {})
        }
    }
}

@Composable
private fun WidgetUiNarrow(
    imageUri: Uri,
    playPauseIcon: PlayPauseIcon,
) {
    Scaffold(titleBar = {} /* title bar will be optional in scaffold in glance 1.1.0-beta3*/) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            AlbumArt(imageUri, GlanceModifier.size(Sizes.imageCondensed))
            Spacer(GlanceModifier.defaultWeight())
            PlayPauseButton(playPauseIcon, {})
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
private fun AlbumArt(
    imageUri: Uri,
    modifier: GlanceModifier = GlanceModifier
) {
    WidgetAsyncImage(
        uri = imageUri,
        contentDescription = null,
        modifier = modifier.cornerRadius(12.dp),
    )
}

@Composable
fun PodcastText(title: String, subtitle: String, modifier: GlanceModifier = GlanceModifier) {
    val fgColor = GlanceTheme.colors.onPrimaryContainer
    Column(modifier) {
        Text(
            text = title,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = fgColor),
            maxLines = 2,
        )
        Text(
            text = subtitle,
            style = TextStyle(fontSize = 14.sp, color = fgColor),
            maxLines = 2,
        )
    }
}
