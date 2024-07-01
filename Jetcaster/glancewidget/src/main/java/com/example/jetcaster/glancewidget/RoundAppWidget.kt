package com.example.jetcaster.glancewidget

import android.content.Context
import android.net.Uri
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import androidx.glance.material3.ColorProviders

class RoundAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = RoundAppWidget()
}

/**
 * A Jetcaster widget that shows a large round image and a play button.
 */
class RoundAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = TEST_DATA

        provideContent {
            GlanceTheme(
                colors = ColorProviders(
                    light = lightColorScheme(),
                    dark = darkColorScheme()
                )
            ) {
                Content(viewState = state)
            }
        }
    }
}

@Composable
private fun Content(viewState: JetcasterAppWidgetViewState) {
    val contentDescription = "${viewState.episodeTitle} - ${viewState.podcastTitle}"

    // We want to fill the smaller of our two axes. Normally in a portrait launcher this will
    // mean filling width. In some setups, such as tablet landscape, we might instead fill height.
    val parentSize = LocalSize.current
    val sizeModifier: GlanceModifier = if (parentSize.width <= parentSize.height) {
        GlanceModifier.fillMaxWidth().wrapContentHeight()
    } else {
        GlanceModifier.fillMaxHeight().wrapContentWidth()
    }

    Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.BottomStart) {
            WidgetAsyncImage(
                uri = viewState.albumArtUri.let(Uri::parse),
                contentDescription = contentDescription,
                circleCrop = true,
                modifier = sizeModifier
            )
            PlayPauseButton(
                state = PlayPauseIcon.from(viewState.isPlaying),
                onClick = {}
            )
        }
    }
}
