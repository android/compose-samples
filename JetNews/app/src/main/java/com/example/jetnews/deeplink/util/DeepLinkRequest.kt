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

package com.example.jetnews.deeplink.util

import android.net.Uri

/**
 * Parse the requested Uri and store it in a easily readable format
 *
 * @param uri the target deeplink uri to link to
 */
internal class DeepLinkRequest(val uri: Uri) {
    /**
     * A list of path segments
     */
    val pathSegments: List<String> = uri.pathSegments

    /**
     * A map of query name to query value
     */
    val queries = buildMap {
        uri.queryParameterNames.forEach { argName ->
            this[argName] = uri.getQueryParameter(argName)!!
        }
    }

    // TODO add parsing for other Uri components, i.e. fragments, mimeType, action
}
