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

package com.example.owl.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.size.PixelSize
import com.example.owl.ui.theme.compositedOnSurface
import dev.chrisbanes.accompanist.coil.AmbientImageLoader
import dev.chrisbanes.accompanist.coil.CoilImage
import okhttp3.HttpUrl

/**
 * A wrapper around [CoilImage] setting a default [contentScale] and loading placeholder.
 */
@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color? = MaterialTheme.colors.compositedOnSurface(0.2f)
) {
    CoilImage(
        data = url,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            if (placeholderColor != null) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(placeholderColor)
                )
            }
        }
    )
}

@Composable
fun ProvideImageLoader(content: @Composable () -> Unit) {
    val context = AmbientContext.current
    val loader = remember(context) {
        ImageLoader.Builder(context)
            .componentRegistry {
                add(UnsplashSizingInterceptor)
            }.build()
    }
    Providers(AmbientImageLoader provides loader, content = content)
}

/**
 * A Coil [Interceptor] which appends query params to Unsplash urls to request sized images.
 */
@OptIn(ExperimentalCoilApi::class)
object UnsplashSizingInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        val size = chain.size
        if (data is String &&
            data.startsWith("https://images.unsplash.com/photo-") &&
            size is PixelSize &&
            size.width > 0 &&
            size.height > 0
        ) {
            val url = HttpUrl.parse(data)!!
                .newBuilder()
                .addQueryParameter("w", size.width.toString())
                .addQueryParameter("h", size.height.toString())
                .build()
            val request = chain.request.newBuilder().data(url).build()
            return chain.proceed(request)
        }
        return chain.proceed(chain.request)
    }
}
