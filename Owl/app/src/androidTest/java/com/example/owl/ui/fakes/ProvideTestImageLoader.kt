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

package com.example.owl.ui.fakes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import dev.chrisbanes.accompanist.coil.AmbientImageLoader

/**
 * Replaces all remote images with a simple black drawable to make testing faster and hermetic.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProvideTestImageLoader(content: @Composable () -> Unit) {

    // From https://coil-kt.github.io/coil/image_loaders/
    val loader = object : ImageLoader {
        private val drawable = ColorDrawable(Color.BLACK)

        private val disposable = object : Disposable {
            override val isDisposed get() = true
            override fun dispose() {}
            override suspend fun await() {}
        }

        override val bitmapPool: BitmapPool = BitmapPool(0)

        override val defaults: DefaultRequestOptions = DefaultRequestOptions()
        override val memoryCache: MemoryCache
            get() = TODO("Not yet implemented")

        override fun enqueue(request: ImageRequest): Disposable {
            // Always call onStart before onSuccess.
            request.target?.onStart(drawable)
            request.target?.onSuccess(drawable)
            return disposable
        }

        override suspend fun execute(request: ImageRequest): ImageResult {
            return SuccessResult(
                drawable = drawable,
                request = request,
                metadata = ImageResult.Metadata(
                    memoryCacheKey = MemoryCache.Key(""),
                    isSampled = false,
                    dataSource = DataSource.MEMORY_CACHE,
                    isPlaceholderMemoryCacheKeyPresent = false
                )
            )
        }

        override fun shutdown() { }
    }
    Providers(AmbientImageLoader provides loader, content = content)
}
