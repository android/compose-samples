/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetcaster.util

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.getValue
import androidx.compose.launchInComposition
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.ui.animation.animate
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.graphics.Color
import coil.Coil
import coil.request.GetRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A custom [State] which stores and caches the result of any calculated dominant colors
 * from images.
 *
 * @param context Android context
 * @param defaultColor The default color, which will be used if [calculateDominantColor] fails to
 * calculate a dominant color
 * @param cacheSize The size of the [LruCache] used to store recent results. Pass `0` to
 * disable the cache.
 */
private class PaletteColorState(
    private val context: Context,
    private val defaultColor: Color,
    cacheSize: Int = 12
) : State<Color> {
    private var colorValue by mutableStateOf(defaultColor)

    private val cache = when {
        cacheSize > 0 -> LruCache<String, Color>(cacheSize)
        else -> null
    }

    override val value: Color
        get() = colorValue

    suspend fun calculateDominantColor(url: String) {
        val cached = cache?.get(url)
        colorValue = when {
            cached != null -> cached
            else -> {
                calculateDominantColorInImage(
                    context = context,
                    imageUrl = url,
                    fallbackColor = defaultColor
                ).also {
                    cache?.put(url, it)
                }
            }
        }
    }

    /**
     * Reset the color value to [defaultColor].
     */
    fun reset() {
        colorValue = defaultColor
    }
}

/**
 * Draws a vertical gradient, using the dominant color in the image at [imageSourceUrl].
 * The gradient is drawn with the opacity given in [opacity].
 */
@Composable
fun DominantColorVerticalGradient(
    imageSourceUrl: String?,
    opacity: Float = 0.38f,
    modifier: Modifier = Modifier
) {
    val context = ContextAmbient.current

    val colorState = remember {
        PaletteColorState(
            context = context,
            defaultColor = Color.Transparent
        )
    }

    if (imageSourceUrl != null) {
        launchInComposition(imageSourceUrl) {
            colorState.calculateDominantColor(imageSourceUrl)
        }
    } else {
        colorState.reset()
    }

    Box(
        modifier = modifier.verticalGradientScrim(
            color = animate(colorState.value.copy(alpha = opacity)),
            startYPercentage = 1f,
            endYPercentage = 0f
        )
    )
}

/**
 * Fetches the given [imageUrl] with [Coil], then uses [Palette] to calculate the dominant color.
 * If the image load fails, [fallbackColor] is returned.
 */
private suspend fun calculateDominantColorInImage(
    context: Context,
    imageUrl: String,
    fallbackColor: Color = Color.Transparent
): Color {
    val r = GetRequest.Builder(context)
        .data(imageUrl)
        // We scale the image to fill 128px x 128px (i.e. min dimension == 128px)
        .size(128)
        .scale(Scale.FILL)
        // Disable hardware bitmaps, since Palette uses Bitmap.getPixels()
        .allowHardware(false)
        .build()

    val result = Coil.execute(r)

    val bitmap = when (result) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }

    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette = Palette.Builder(bitmap)
                // Disable any bitmap resizing in Palette. We've already loaded an appropriately
                // sized bitmap through Coil
                .resizeBitmapArea(0)
                // Clear any built-in filters. We want the unfiltered dominant color
                .clearFilters()
                .generate()

            palette.dominantSwatch?.let { Color(it.rgb) }
        }
    } ?: fallbackColor
}
