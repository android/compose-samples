/*
 * Copyright 2019 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui

import android.content.res.Resources
import androidx.ui.graphics.imageFromResource
import com.example.jetnews.R
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Defining and loading here all the images that we use from resources.
 */
class Icons(resources: Resources) {
    val appLogo by lazy(NONE) { imageFromResource(resources, R.drawable.jp_news_logo) }
    val home by lazy(NONE) { imageFromResource(resources, R.drawable.baseline_home_black_24dp) }
    val menu by lazy(NONE) { imageFromResource(resources, R.drawable.baseline_menu_white_24dp) }
    val more by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_more_vert_black_24dp)
    }
    val back by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_arrow_back_white_24dp)
    }
    val bookmarkOn by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_bookmark_black_24dp)
    }
    val bookmarkOff by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_bookmark_border_black_24dp)
    }
    val heartOn by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_favorite_black_24dp)
    }
    val heartOff by lazy(NONE) {
        imageFromResource(resources, R.drawable.baseline_favorite_border_black_24dp)
    }
    val share by lazy(NONE) { imageFromResource(resources, R.drawable.baseline_share_black_24dp) }
    val placeholder_1_1 by lazy(NONE) { imageFromResource(resources, R.drawable.placeholder_1_1) }
    val placeholder_4_3 by lazy(NONE) { imageFromResource(resources, R.drawable.placeholder_4_3) }
}
