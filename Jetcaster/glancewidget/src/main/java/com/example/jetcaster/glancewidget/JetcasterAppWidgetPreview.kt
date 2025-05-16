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

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.compose
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private object SizesPreview {
    val medium = 56.dp
}

/**
 * This is a convenience function for updating the widget preview using Generated Previews.
 *
 * In a real application, this would be called whenever the widget's state changes.
 */
fun updateWidgetPreview(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appwidgetManager = AppWidgetManager.getInstance(context)

                appwidgetManager.setWidgetPreview(
                    ComponentName(context, JetcasterAppWidgetReceiver::class.java),
                    AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                    JetcasterAppWidgetPreview().compose(
                        context,
                        size = DpSize(160.dp, 64.dp),
                    ),
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }
        }
    }
}

class JetcasterAppWidgetPreview : GlanceAppWidget() {
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Widget()
            }
        }
    }
}

@Composable
private fun Widget() {
    Scaffold {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Image(
                modifier = GlanceModifier.wrapContentSize().size(SizesPreview.medium),
                provider = ImageProvider(R.drawable.widget_preview_thumbnail),
                contentDescription = "",
            )
            Spacer(GlanceModifier.defaultWeight())
            SquareIconButton(
                modifier = GlanceModifier.size(SizesPreview.medium),
                imageProvider = ImageProvider(R.drawable.outline_play_arrow_24),
                contentDescription = "",
                onClick = { },
            )
        }
    }
}
