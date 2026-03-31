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

package com.example.jetnews.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.example.jetnews.R
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.interests.InterestsKey

data class NavigationItem(
    val navKey: NavKey,
    @DrawableRes val iconResourceId: Int,
    @StringRes val iconContentDescriptionResourceId: Int,
    @StringRes val labelResourceId: Int,
)

val NAVIGATION_ITEMS = listOf(
    NavigationItem(
        HomeKey,
        R.drawable.ic_home,
        R.string.cd_navigate_home,
        R.string.home_title,
    ),
    NavigationItem(
        InterestsKey,
        R.drawable.ic_list_alt,
        R.string.cd_navigate_interests,
        R.string.interests_title,
    ),
)
