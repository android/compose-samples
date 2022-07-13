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

package com.example.jetcaster

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

/**
 * Application which sets up our dependency [Graph] with a context.
 */
class JetcasterApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Disable `Cache-Control` header support as some podcast images disable disk caching.
            .respectCacheHeaders(false)
            .build()
    }
}
