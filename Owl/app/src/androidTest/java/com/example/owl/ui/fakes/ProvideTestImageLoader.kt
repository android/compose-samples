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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import coil.compose.LocalImageLoader
import coil.decode.DataSource
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult

/**
 * Replaces all remote images with a simple black drawable to make testing faster and hermetic.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProvideTestImageLoader(content: @Composable () -> Unit) {
    // From https://coil-kt.github.io/coil/image_loaders/
    val context = LocalContext.current
    val loader = remember {
        object : ImageLoader {
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
                request.target?.onStart(placeholder = null)
                request.target?.onSuccess(result = ColorDrawable(Color.BLACK))
                return disposable
            }

            override suspend fun execute(request: ImageRequest): ImageResult {
                return SuccessResult(
                    drawable = ColorDrawable(Color.BLACK),
                    request = request,
                    metadata = ImageResult.Metadata(
                        memoryCacheKey = MemoryCache.Key(""),
                        isSampled = false,
                        dataSource = DataSource.MEMORY_CACHE,
                        isPlaceholderMemoryCacheKeyPresent = false
                    )
                )
            }

            override fun newBuilder() = ImageLoader.Builder(context)

            override fun shutdown() {}
        }
    }
    CompositionLocalProvider(LocalImageLoader provides loader, content = content)
}
