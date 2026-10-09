/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.jetchat.conversation

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.collection.mutableIntFloatMapOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources

private val aspectRatioCache = mutableIntFloatMapOf()

/**
 * Width / height of a raster drawable, read from the image header only. Lets message bubbles size
 * themselves before the image is decoded asynchronously, so the layout matches a synchronous load.
 */
@Composable
fun rememberImageAspectRatio(@DrawableRes imageRes: Int): Float {
    val resources = LocalResources.current
    return remember(imageRes, resources) {
        aspectRatioCache.getOrPut(imageRes) { readAspectRatio(resources, imageRes) }
    }
}

private fun readAspectRatio(resources: Resources, @DrawableRes imageRes: Int): Float {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeResource(resources, imageRes, options)
    return if (options.outWidth > 0 && options.outHeight > 0) {
        options.outWidth.toFloat() / options.outHeight
    } else {
        1f
    }
}
