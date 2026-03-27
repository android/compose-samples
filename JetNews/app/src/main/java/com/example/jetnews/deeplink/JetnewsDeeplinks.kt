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

package com.example.jetnews.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.deeplink.util.DeepLinkMatcher
import com.example.jetnews.deeplink.util.DeepLinkRequest
import com.example.jetnews.deeplink.util.KeyDecoder
import com.example.jetnews.ui.home.HomeDeepLinkPattern
import com.example.jetnews.ui.interests.InterestsDeepLinkPattern
import com.example.jetnews.ui.navigation.DeepLinkKey
import com.example.jetnews.ui.post.PostDeepLinkPattern

val JetnewsDeepLinkPatterns = listOf(HomeDeepLinkPattern, PostDeepLinkPattern, InterestsDeepLinkPattern)

fun Uri.handleDeepLink(): List<NavKey>? {
    val deepLinkRequest = DeepLinkRequest(this)

    val deepLinkMatchResult = JetnewsDeepLinkPatterns.firstNotNullOfOrNull {
        DeepLinkMatcher(deepLinkRequest, it).match()
    } ?: return null

    val initialKey = KeyDecoder(deepLinkMatchResult.args).decodeSerializableValue(deepLinkMatchResult.serializer)

    return generateSequence(initialKey) { (it as? DeepLinkKey)?.parent }
        .toList()
        .asReversed()
}
